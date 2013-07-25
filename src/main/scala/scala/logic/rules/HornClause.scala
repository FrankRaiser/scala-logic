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
package scala.logic.rules

import scala.logic.Term
import scala.language.existentials

/**
 * A HornClause rule can only have one head predicate and no guard.
 * @author Frank Raiser
 */
class HornClause(val headPredicate : Term, val body : Seq[Term] = Nil) extends Rule {
  val guard = Nil
  val head = List(headPredicate)

  override val toString = headPredicate + " :- " + body
}
