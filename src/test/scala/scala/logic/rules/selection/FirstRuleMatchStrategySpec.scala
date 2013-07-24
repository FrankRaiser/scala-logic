package scala.logic.rules.selection

import org.specs2.mutable.Specification
import org.junit.runner.RunWith
import org.specs2.runner.JUnitRunner
import org.specs2.specification.Scope
import scala.logic.state.TermState
import scala.logic.rules.PrologRule
import scala.logic._
import scala.logic.rules.Rule
import scala.logic.state.TermState
import scala.logic.state.State

@RunWith(classOf[JUnitRunner])
object FirstRuleMatchStrategySpec extends Specification {
  trait data extends Scope {
    val ouT = new FirstRuleMatchStrategy with State.Terms with Rule.Prolog {}
    
    val fx = "f(X)".asTerm
    
    val emptyState = new TermState(Nil)
    val state = new TermState(List(fx))
    
    val rule1 = new PrologRule(fx, Nil)
    
    val rules = List(rule1)
  }
    
  "The first rule match strategy" should {
    "not find a rule in an empty ruleset" in new data {
      ouT.selectRule (emptyState, Nil) must beNone
    }
    
    "not find a rule for an empty state" in new data {
      ouT.selectRule (emptyState, rules) must beNone
    }
    
    "find the only available rule" in new data {
      skipped("Not implemented yet")
      ouT.selectRule (state, List(rule1)) must be equalTo(Some(rule1))
    }
  }
}