package scala.logic.rules.application

import scala.logic.state.State
import scala.logic.SemanticStrategy
import scala.logic.rules.Rule

/**
 * A strategy for checking if a rule is applicable in a given state and 
 * to apply it in order to get a result state.
 * 
 * Precondition: the rules can be assumed to use fresh variables. 
 * 
 * @author Frank Raiser
 */
trait RuleApplicationStrategy {

  def isApplicable(rule : Rule, state : State) : Boolean
  
  def applyRule(rule : Rule, state : State) : State
}