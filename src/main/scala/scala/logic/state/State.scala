// Copyright (C) 2013 Frank Raiser
// See the LICENCE file distributed with this work for additional
// information regarding copyright ownership.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
package scala.logic.state

import scala.logic._
import scala.logic.disjoint.DisjointSets
import scala.logic.exception.UnificationException
import scala.util.Random

/**
 * A state that a program execution can be in. The state is
 * also responsible to keep track of variable bindings, which
 * may change in-between states.
 *
 * States should be immutable
 *
 * @author Frank Raiser
 */
trait State {

  /** a map of variables bound to their respective terms */
  def variableBindings : State.VariableBinding

  /** disjoint sets to keep track of variables bound to each other */
  def disjointSets : DisjointSets[Var]

  def allVariables : Iterable[Var] = disjointSets.nodes.keys

  def findTermsThatMatch(otherTerm : Term) : Stream[Term]

  def boundTerm(variable : Var) : Option[Term] =
    disjointSets.find(variable) flatMap variableBindings.get

  /** A size measure of the state */
  def size : Int

  /** extends the state by an additional binding */
  def bind(variable : Var, term : Term) : State

  // scalastyle:off
  /** add new terms */
  def ++ (terms : Seq[Term]) : State
  /** remove terms */
  def -- (terms : Seq[Term]) : State
  // scalastyle:on

  /** remove all terms (but keep variable bindings) */
  def clear : State

  /** adds the given variables to the state's DisjointSets */
  def addVariables(variables : Seq[Var]) : State

  def getFreshNameWithPrefix(prefix : String) : String = {
    def getRandomSuffix = ("%0" + State.RANDOM_SUFFIX_LENGTH + "d").format(
      (math.abs(Random.nextInt) % math.pow(10, State.RANDOM_SUFFIX_LENGTH).toInt))

    var name : String = ""
    val variables = allVariables.map(_.name).toList
    do {
      name = prefix + getRandomSuffix
    }
    while (variables.contains(name))

    name
  }
}

object State {
  type VariableBinding = Map[Var, Term]

  val RANDOM_SUFFIX_LENGTH = 7
}
