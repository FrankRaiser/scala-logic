package scala.logic

import org.specs2.mutable.Specification
import org.junit.runner.RunWith
import org.specs2.runner.JUnitRunner
import org.specs2.specification.Scope
import scala.logic.exception.UnificationException
import scala.logic.state.TermState
import scala.logic.state.State

@RunWith(classOf[JUnitRunner])
object FreshTermsSpec extends Specification {
  trait data extends Scope {
    implicit val state = new TermState(Nil)
    val x = new Var("X")
    val y = new Var("Y")
    val z = new Var("Z")
    val a = new Constant("a")
    val b = new Constant("b")
    val c = new Constant("c")
  }
  
  def notBeUnifiable = throwA[Exception].like { case ue : UnificationException => 1 === 1 }
  def beUnifiable = throwA[Throwable].not

  "Fresh terms" should {
    "be the original term for constants" in new data {
      a.fresh must be equalTo(a)
    }
    "be the original term for ground n-ary terms" in new data {
      for (term <- List(
          "f(a)", "f(a,b)", "f(a,b,c)", "f(g(a),h(b,c))")
          .map(_.asTerm)) {
        term.fresh must be equalTo(term)
      }
    }
    "be a new unused variable for each time x.fresh is called" in {
      implicit var context : State = new TermState(Nil)
      val x = new Var("X")
      for (i <- 1 to 1000) {
        val fresh = x.fresh
        fresh must not be equalTo(x)
        context = context.addVariables(fresh.variables.toSeq)
        context.allVariables must haveSize(i)
      }
      success
    }
    "create fresh variable once for multiple occurences (f.ex. f(X,X))" in {
      val fxx = "f(X,X)".asTerm
      implicit var context = new TermState(Nil).addVariables(fxx.variables.toSeq)
      val res = fxx.fresh
      res.arguments(0).equals(res.arguments(1)) must beTrue
      context = context.addVariables(res.variables.toSeq)
      context.allVariables must haveSize(2) 
    }
    "fresh variable names should not get longer each time" in new data {
      val res1 = x.fresh.asInstanceOf[Var]
      res1.fresh.asInstanceOf[Var].name.size must be equalTo(res1.name.size)
    }
    "work for complex example like f(X, g(a, X), h(f(Y)))" in new data {
      val f = "f(X, g(a, X), h(f(Y)))".asTerm.fresh
      f.arguments(1).asInstanceOf[Term].arguments(1) must be equalTo(f.arguments(0))
      f.arguments(0).asInstanceOf[Var].name must not be equalTo("X")
      val ny = f.arguments(2).arguments(0).arguments(0).asInstanceOf[Var]
      f.arguments(0).equals(ny) must beFalse
      ny.name must not be equalTo("X")
      ny.name must not be equalTo("Y")
    }
    "create fresh varnames for longer var names" in {
      implicit var context : State = new TermState(Nil)
      val name = "SomethingLongerThanUsualForAVariableName"
      val fresh = name.asTerm.fresh
      fresh.asInstanceOf[Var].name must not be equalTo(name)
      context = context.addVariables(fresh.variables.toSeq)
      context.allVariables must haveSize(1)
    }
  }
}