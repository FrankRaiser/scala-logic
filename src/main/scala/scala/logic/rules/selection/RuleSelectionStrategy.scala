package scala.logic.rules.selection

import scala.logic.rules.Rule
import scala.logic.state.State
import scala.logic.rules.application.RuleApplicationStrategy
import scala.logic.SemanticStrategy

/**
 * A strategy for selecting a rule from a given collection of rules
 * and a current state.
 * 
 * Precondition: the given available rules can be assumed to be fresh.
 * 
 * @author Frank Raiser
 */
trait RuleSelectionStrategy { self: SemanticStrategy =>
  
  def ruleApplicationStrategy : RuleApplicationStrategy
  
  /**
   * Select one of the available rules that can be applied to the given state, according
   * to the current rule application strategy.
   * @param state the current state, to which a rule shall be applied
   * @param availableRules a list of rules, that could potentially be applied
   * @return None if no rule is selectable, otherwise Some(rule) 
   * for with <code>applicationStrategy.isApplicable(rule, state)==true</code> 
   */
  def selectRule(state : StateType, availableRules : List[RuleType]) : Option[RuleType]
}