package scala.logic.rules.application

import scala.logic.state.State
import scala.logic.rules.Rule


/**
 * A strategy for checking if a rule is applicable in a given state and 
 * to apply it in order to get a result state.
 * 
 * @author Frank Raiser
 */
trait RuleApplicationStrategy {

  type StateType <: State
  type RuleType <: Rule
  
  def isApplicable(rule : Rule, state : State) : Boolean
  
  def applyRule(rule : Rule, state : State) : State
  
}