package scala.logic

import scala.logic.state.TermState
import scala.logic.rules.application.TermRewritingStrategy
import scala.logic.rules.HornClause
import scala.logic.rules.SimpleTermRule
import scala.logic.rules.selection.FirstRuleMatchStrategy

object Semantics {
  trait HornClauses extends SemanticStrategy {
    type StateType = TermState
    type RuleType = HornClause
  }

  trait SimpleTerms extends SemanticStrategy {
    type StateType = TermState
    type RuleType = SimpleTermRule
  }

}