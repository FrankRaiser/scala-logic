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
 * A constant is a term of arity 0, but we provide some support to
 * simply working with constants.
 * @author Frank Raiser
 */
case class Constant(val value : String) extends Term {

  val arity = 0
  val symbol = value
  val arguments : List[Term] = Nil

  override def isGround(implicit context : State) : Boolean = true

  override def occurs(v : Var) : Boolean = false

  override val toString = value

  override def makeFreshTermWithVariables(
      freshVars : VariableSubstitution = Map.empty)(implicit context : State) : (Term, VariableSubstitution) =
        (this, freshVars)
}
