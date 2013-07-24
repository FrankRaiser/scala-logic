package scala.logic.rules.application

import org.specs2.mutable.Specification
import org.junit.runner.RunWith
import org.specs2.runner.JUnitRunner
import org.specs2.specification.Scope
import scala.logic._
import scala.logic.state.TermState
import scala.logic.rules.SimpleTermRule

@RunWith(classOf[JUnitRunner])
object TermRewritingStrategySpec extends Specification {
  
  trait data extends Scope {
    val ouT : RuleApplicationStrategy = new TermRewritingStrategy {
      type StateType = TermState
      type RuleType = SimpleTermRule
    }
    
    val fa = "f(a)".asTerm
    val fx = "f(X)".asTerm
    val x = new Var("X")
    val state = new TermState(List(fx)).bind(x, "a".asTerm)
    
    val twoHeadedRule = new SimpleTermRule(List(fa, "f(b)".asTerm))
    val removefaRule = new SimpleTermRule(List(fa))
    val replacefgRule = new SimpleTermRule(List("f(Y)".asTerm), List("g(Y)".asTerm))
  }
    
  "The term rewriting strategy" should {
    "not be applicable to a rule with multiple head terms" in new data {
      ouT.isApplicable(twoHeadedRule, state) must beFalse
    }
    "state(f(a)) must be applicable to rule f(a) -> T" in new data {
      ouT.isApplicable(removefaRule, new TermState(List(fa))) must beTrue
    }
    "state(f(X), X=a) must be applicable to rule f(a) -> T" in new data {
      state.findTermsThatMatch(fa) must not beEmpty
      
      ouT.isApplicable(removefaRule, state) must beTrue
    }
    "state(f(b)) must not be applicable to rule f(a) -> T" in new data {
      ouT.isApplicable(removefaRule, new TermState(List("f(b)".asTerm))) must beFalse
    }
    "state(f(a)) must be applicable to rule f(Y) -> g(Y)" in new data {
      ouT.isApplicable(replacefgRule, state) must beTrue
    }
  }
}