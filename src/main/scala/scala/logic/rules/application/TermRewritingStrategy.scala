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
package scala.logic.rules.application

import scala.logic.rules.Rule
import scala.logic.state.State
import scala.logic.unification.Unifier
import scala.logic.SemanticStrategy
import scala.logic.Semantics

/**
 * A standard term rewriting strategy, which matches a single-headed
 * rule and rewrites the corresponding term to the rule body (without
 * any guard support)
 *
 * @author Frank Raiser
 */
trait TermRewritingStrategy extends RuleApplicationStrategy {

  def isApplicable(rule : Rule, state : State) : Boolean =
    rule.guard.isEmpty && rule.head.size == 1 && !state.findTermsThatMatch(rule.head.head).isEmpty

  def applyRule(rule : Rule, state : State) : State = state.findTermsThatMatch(rule.head.head).headOption match {
    case None => throw new RuntimeException("Rule is not applicable. Call isApplicable first!")
    case Some(term) => Unifier.matchTerms(rule.head.head, term, state).toOption match {
      case None => throw new RuntimeException("Rule is not applicable. Unexpected match error")
      case Some(matchedState) =>
        matchedState -- List(term) ++ rule.body
    }
  }
}
