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
package scala.logic.rules.selection

import scala.logic.rules.Rule
import scala.logic.state.State
import scala.logic.rules.application.RuleApplicationStrategy

/**
 * A strategy for selecting a rule from a given collection of rules
 * and a current state.
 *
 * Precondition: the given available rules can be assumed to be fresh.
 *
 * @author Frank Raiser
 */
trait RuleSelectionStrategy { self : RuleApplicationStrategy =>

  /**
   * Select one of the available rules that can be applied to the given state, according
   * to the current rule application strategy.
   * @param state the current state, to which a rule shall be applied
   * @param availableRules a list of rules, that could potentially be applied
   * @return None if no rule is selectable, otherwise Some(rule)
   * for with <code>applicationStrategy.isApplicable(rule, state)==true</code>
   */
  def selectRule(state : State, availableRules : List[Rule]) : Option[Rule]
}
