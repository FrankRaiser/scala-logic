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
trait FirstRuleMatchStrategy extends RuleSelectionStrategy { self : SemanticStrategy =>
  
  def selectRule(store : StateType, availableRules : List[RuleType]): Option[RuleType] = None 
}