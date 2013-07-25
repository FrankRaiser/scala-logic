package scala.logic.rules.selection

import scala.logic.state.State
import scala.logic.rules.Rule
import scala.logic.rules.application.RuleApplicationStrategy

/**
 * A simple strategy, which tries rules in their given order and selects
 * the first that matches to the current state.
 * 
 * @author Frank Raiser
 */
trait FirstRuleMatchStrategy extends RuleSelectionStrategy { self : RuleApplicationStrategy =>
  
  def selectRule(state : State, availableRules : List[Rule]): Option[Rule] =
    availableRules.par.find(rule => isApplicable(rule, state)) 
}