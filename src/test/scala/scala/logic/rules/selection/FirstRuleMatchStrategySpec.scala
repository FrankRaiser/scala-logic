package scala.logic.rules.selection

import org.specs2.mutable.Specification
import org.junit.runner.RunWith
import org.specs2.runner.JUnitRunner
import org.specs2.specification.Scope
import scala.logic.state.TermState
import scala.logic.rules.HornClause
import scala.logic._
import scala.logic.rules.Rule
import scala.logic.state.TermState
import scala.logic.state.State
import scala.logic.rules.application.TermRewritingStrategy
import scala.logic.rules.SimpleTermRule

@RunWith(classOf[JUnitRunner])
object FirstRuleMatchStrategySpec extends Specification {
  val ouT = new FirstRuleMatchStrategy with Semantics.SimpleTerms with TermRewritingStrategy {}
  
  trait data extends Scope {
    
    val fa = "f(a)".asTerm
    
    val emptyState = new TermState(Nil)
    val state = new TermState(List(fa))
    
    val rule1 = new SimpleTermRule(List(fa))
    val rule2 = new SimpleTermRule(List("g(a)".asTerm))
    val rule3 = new SimpleTermRule(List("f(a)".asTerm))
    
    val rules = List(rule2, rule1, rule3)
  }
    
  "The first rule match strategy" should {
    "not find a rule in an empty ruleset" in new data {
      ouT.selectRule (emptyState, Nil) must beNone
    }
    
    "not find a rule for an empty state" in new data {
      ouT.selectRule (emptyState, rules) must beNone
    }
    
    "find the only available rule" in new data {
      ouT.selectRule (state, List(rule1)) must be equalTo(Some(rule1))
    }
    "select second rule, if first not applicable" in new data {
      ouT.selectRule(state, List(rule2, rule1)) must be equalTo(Some(rule1))
    }
    "select first matching rule out of three" in new data {
      ouT.selectRule (state, rules) must be equalTo(Some(rule1))
    }
  }
}