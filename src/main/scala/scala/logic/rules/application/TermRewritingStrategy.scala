package scala.logic.rules.application

import scala.logic.rules.Rule
import scala.logic.state.State
import scala.logic.unification.Unifier
import scala.logic.SemanticStrategy
import scala.logic.Semantics

/**
 * A standard term rewriting strategy, which matches a single-headed
 * rule and rewrites the corresponding term to the rule body (without
 * any guard support)
 * 
 * @author Frank Raiser
 */
trait TermRewritingStrategy extends RuleApplicationStrategy { self : Semantics.SimpleTerms =>

  def isApplicable(rule : Rule, state : State) : Boolean =
    rule.guard.isEmpty && rule.head.size == 1 && !state.findTermsThatMatch(rule.head.head).isEmpty
  
  def applyRule(rule : Rule, state : State) : State= state.findTermsThatMatch(rule.head.head).headOption match {
    case None => throw new RuntimeException("Rule is not applicable. Call isApplicable first!")
    case Some(term) => Unifier.matchTerms(rule.head.head, term, state).toOption match {
      case None => throw new RuntimeException("Rule is not applicable. Unexpected match error")
      case Some(matchedState) =>
        matchedState -- List(term) ++ rule.body
    }
  }
}