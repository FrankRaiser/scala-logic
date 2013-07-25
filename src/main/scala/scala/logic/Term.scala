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
package scala.logic

import scala.logic.state.State

/**
 * A general n-ary term. Terms that can be evaluated can be constructed by
 * extending both Term and Function0 and overriding the apply method accordingly.
 * @author Frank Raiser
 */
trait Term { self =>
  type VariableSubstitution = Map[Var, Var]

  def symbol : String
  def arity : Int
  def arguments : List[Term]

  def isGround(implicit context : State) : Boolean = arguments.map(_.isGround(context)).reduce(_ && _)

  /**
   * Occur-check to test if the given variable occurs in any sub-term
   */
  def occurs(variable : Var) : Boolean =
    arguments.find(_.occurs(variable) == true) != None

  /** Recursively retrieves all variables contained somewhere in this term */
  def variables : Set[Var] = (arguments flatMap (_.variables)).toSet

  /** Create a fresh copy of this term, i.e. a copy in which all
   * variables are replaced by new unused variables.
   */
  def fresh(implicit context : State) : Term = makeFreshTermWithVariables(Map.empty)(context)._1

  def makeFreshTermWithVariables(
      freshVars : VariableSubstitution = Map.empty)(implicit context : State) : (Term, VariableSubstitution) = {
    var sub : VariableSubstitution = freshVars

    def extractAndSub(arg : Term) = {
      val (freshArg, freshSub) = arg.makeFreshTermWithVariables(sub)(context)
      sub = freshSub
      freshArg
    }
    val origTerm = this
    val freshArgs = arguments.map(extractAndSub(_))
    (new Term() {
      val arguments = freshArgs
      val arity = origTerm.arity
      val symbol = origTerm.symbol
    }, sub)
  }

  /** returns the current term, after all variable substitutions
   * have been applied to it.
   */
  def substituted(implicit context : State) : Term = new Term {
    val symbol = self.symbol
    val arity = self.arity
    val arguments = self.arguments.map(_.substituted(context))
  }

  override def toString : String = arity match {
    case 0 => symbol
    case _ => symbol + "(" + arguments.map(_.toString).mkString(", ") + ")"
  }

  override def equals(other : Any) : Boolean = other match {
    case t : Term =>
      symbol == t.symbol &&
      arguments.zip(t.arguments).find(p => p._1 != p._2) == None
    case _ => false // covered, but not detected by scct due to a bug :(
  }

  override def hashCode() : Int = {
    val prime = 41
    prime + symbol.hashCode() + arguments.map(_.hashCode).sum
  }
}
