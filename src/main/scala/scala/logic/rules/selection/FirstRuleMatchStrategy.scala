package scala.logic.rules.selection

/**
 * A simple strategy, which tries rules in their given order and selects
 * the first that matches to the current state.
 * 
 * @author Frank Raiser
 */
trait FirstRuleMatchStrategy extends RuleSelectionStrategy {

  override def selectRule(store : StateType, availableRules : List[RuleType]) : Option[RuleType] = None 
}