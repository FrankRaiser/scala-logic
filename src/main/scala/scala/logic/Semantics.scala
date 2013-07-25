package scala.logic

import scala.logic.state.TermState
import scala.logic.rules.application.TermRewritingStrategy
import scala.logic.rules.PrologRule
import scala.logic.rules.SimpleTermRule
import scala.logic.rules.selection.FirstRuleMatchStrategy

object Semantics {
  trait Prolog {
    type RuleType = PrologRule
  }

  trait SimpleTerms extends SemanticStrategy {
    type StateType = TermState
    type RuleType = SimpleTermRule
  }
  
  trait TermRewriting { self : SimpleTerms => 
    val ruleApplicationStrategy = new TermRewritingStrategy() with SimpleTerms
  }
}