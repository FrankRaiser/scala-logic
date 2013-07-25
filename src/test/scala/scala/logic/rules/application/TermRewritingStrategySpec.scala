package scala.logic.rules.application

import org.specs2.mutable.Specification
import org.junit.runner.RunWith
import org.specs2.runner.JUnitRunner
import org.specs2.specification.Scope
import scala.logic._
import scala.logic.state.TermState
import scala.logic.rules.SimpleTermRule
import scala.logic.rules.SimpleTermRule
import scala.logic.state.TermState
import scala.logic.rules.SimpleTermRule
import scala.logic.state.TermState

@RunWith(classOf[JUnitRunner])
object TermRewritingStrategySpec extends Specification {
  val ouT : RuleApplicationStrategy = new TermRewritingStrategy with Semantics.SimpleTerms {}
    
  trait data extends Scope {
    
    
    val fa = "f(a)".asTerm
    val fx = "f(X)".asTerm
    val x = new Var("X")
    val state = new TermState(List(fx)).bind(x, "a".asTerm)
    
    val twoHeadedRule = new SimpleTermRule(List(fa, "f(b)".asTerm))
    val removefaRule = new SimpleTermRule(List(fa))
    val replacefagaRule = new SimpleTermRule(List("f(a)".asTerm), List("g(a)".asTerm))
    val replacefgRule = new SimpleTermRule(List("f(Y)".asTerm), List("g(Y)".asTerm))
  }
    
  "The term rewriting strategy" should {
    "not be applicable to a rule with multiple head terms" in new data {
      ouT.isApplicable(twoHeadedRule, state) must beFalse
    }
    "f(a) must be applicable to rule f(a) -> T" in new data {
      ouT.isApplicable(removefaRule, new TermState(List(fa))) must beTrue
    }
    "f(X), X=a must be applicable to rule f(a) -> T" in new data {
      ouT.isApplicable(removefaRule, state) must beTrue
    }
    "f(b) must not be applicable to rule f(a) -> T" in new data {
      ouT.isApplicable(removefaRule, new TermState(List("f(b)".asTerm))) must beFalse
    }
    "f(a) must be applicable to rule f(Y) -> g(Y)" in new data {
      ouT.isApplicable(replacefgRule, state) must beTrue
    }
    "f(a) should be removed when applying f(a) -> T" in new data {
      ouT.applyRule(removefaRule, new TermState(List(fa))).size must be equalTo(0)
    }
    "throw RuntimeException for unapplicable rule" in new data {
      ouT.applyRule(removefaRule, new TermState(Nil)) must throwA[RuntimeException]
    }
    "f(a) should be rewritten to g(a) by f(a) -> g(a)" in new data {
      ouT.applyRule(replacefagaRule, new TermState(List(fa))).findTermsThatMatch("g(a)".asTerm) must not beEmpty
    }
    "f(a) should be rewritten to g(a) by f(Y) -> g(Y)" in new data {
      ouT.applyRule(replacefgRule, new TermState(List(fa))).findTermsThatMatch("g(a)".asTerm) must not beEmpty
    }
    "f(g(X, a), X) -> h(X, b) rewrites f(g(b, Y), Z), Y=a,Z=b to h(b,b)" in {
      val rule = new SimpleTermRule(List("f(g(X,a), X)".asTerm), List("h(X, b)".asTerm))
      val state = new TermState(List("f(g(b, Y), Z)".asTerm)).bind(new Var("Y"), "a".asTerm).bind(new Var("Z"), "b".asTerm)
      ouT.isApplicable(rule, state) must beTrue
      ouT.applyRule(rule, state).findTermsThatMatch("h(b,b)".asTerm) must not beEmpty
    }
    "f(Y) -> h(Y,Y) must rewrite f(a) to h(a,a)" in new data {
      val rule = new SimpleTermRule(List("f(Y)".asTerm), List("h(Y,Y)".asTerm))
      ouT.applyRule(rule, new TermState(List(fa))).findTermsThatMatch("h(a,a)".asTerm) must not beEmpty
    }
  }
}