package scala.logic.rules.selection

import scala.logic.rules.Rule
import scala.logic.state.State

/**
 * A strategy for selecting a rule from a given collection of rules
 * and a current state.
 * 
 * @author Frank Raiser
 */
trait RuleSelectionStrategy {

  type StateType <: State
  type RuleType <: Rule
  
  def selectRule(state : StateType, availableRules : List[RuleType]) : Option[RuleType]
}

object RuleSelectionStrategy {
  trait FirstMatched {
    type RuleSelectionStrategyType = FirstRuleMatchStrategy
  }
}