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

import scala.logic.Term
import scala.logic.disjoint.DisjointSets
import scala.logic.Var
import scala.logic.unification.Unifier

/**
 * A simple state, which holds a list of terms.
 *
 * @author Frank Raiser
 */
case class TermState(
    val terms : List[Term],
    val variableBindings : State.VariableBinding = Map.empty,
    val disjointSets : DisjointSets[Var] = new DisjointSets[Var](Map.empty)) extends State {

  // scalastyle:off
  def ++(otherTerms : Seq[Term]) : State = new TermState(
  // scalastyle:on
      terms ++ otherTerms,
      variableBindings,
      disjointSets ++ otherTerms.flatMap(_.variables)
      )

  // scalastyle:off
  def --(otherTerms : Seq[Term]) : State = new TermState(
  // scalastyle:on
      terms.filterNot(otherTerms.contains), variableBindings, disjointSets)

  def clear : State = new TermState(Nil, variableBindings, disjointSets)

  val size = terms.size

  def bind(variable : Var, term : Term) : State = disjointSets.find(variable) match {
    case Some(rootVar) =>  term match {
      case v : Var => TermState(terms, variableBindings, (disjointSets + v).union(rootVar, v))
      case _ => TermState(terms, variableBindings + (variable -> term), disjointSets)
    }
    case _ => term match {
      case v : Var => TermState(terms, variableBindings, (disjointSets ++ List(variable, v)).union(variable, v))
      case _ => TermState(terms, variableBindings + (variable -> term), disjointSets + variable)
    }
  }

  def addVariables(variables : Seq[Var]) : State =
    TermState(terms, variableBindings, disjointSets ++ variables)

  def findTermsThatMatch(otherTerm : Term) : Stream[Term] =
    terms.toStream.filter(
        term => Unifier.matchTerms(otherTerm, term, this).toOption != None)
}
