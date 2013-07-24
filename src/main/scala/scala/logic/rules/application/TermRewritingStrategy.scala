package scala.logic.rules.application

import scala.logic.rules.Rule
import scala.logic.state.State

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
  
  def applyRule(rule : Rule, state : State) : State = state
}