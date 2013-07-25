package scala.logic.rules.selection

import scala.logic.state.State
import scala.logic.rules.Rule
import scala.logic.SemanticStrategy
import scala.logic.SemanticStrategy

/**
 * A simple strategy, which tries rules in their given order and selects
 * the first that matches to the current state.
 * 
 * @author Frank Raiser
 */
trait FirstRuleMatchStrategy extends RuleSelectionStrategy {
  
  def selectRule(state : State, availableRules : List[Rule]): Option[Rule] =
    availableRules.par.find(rule => ruleApplicationStrategy.isApplicable(rule, state)) 
}