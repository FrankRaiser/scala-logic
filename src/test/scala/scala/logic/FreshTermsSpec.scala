package scala.logic

import org.specs2.mutable.Specification
import org.junit.runner.RunWith
import org.specs2.runner.JUnitRunner
import org.specs2.specification.Scope
import scala.logic.exception.UnificationException

@RunWith(classOf[JUnitRunner])
object FreshTermsSpec extends Specification {
  trait data extends Scope {
    implicit val variableStore = new VariableStore
    val x = Var[Any]("X")
    val y = Var[Any]("Y")
    val z = Var[Any]("Z")
    val a = Constant[Any]("a")
    val b = Constant[Any]("b")
    val c = Constant[Any]("c")
  }
  
  def notBeUnifiable = throwA[Exception].like { case ue : UnificationException[_] => 1 === 1 }
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
    "be a new unused variable for each time x.fresh is called" in new data {
      val varsBefore = variableStore.allVariables.size
      for (i <- 1 to 1000) {
        x.fresh must not be equalTo(x)
        variableStore.allVariables.size must be equalTo(varsBefore+i)
      }
    }
    "create fresh variable once for multiple occurences (f.ex. f(X,X))" in new data {
      val varsBefore = variableStore.allVariables.size
      val res = "f(X,X)".asTerm.fresh.asInstanceOf[Term2[_,_,_]]
      res.arg1.equals(res.arg2) must beTrue
      variableStore.allVariables.size must be equalTo(varsBefore+1)
    }
    "fresh variable names should not get longer each time" in new data {
      val res1 = x.fresh.asInstanceOf[Var[_]]
      res1.fresh.asInstanceOf[Var[_]].name.size must be equalTo(res1.name.size)
    }
    "work for complex example like f(X, g(a, X), h(f(Y)))" in new data {
      val f = "f(X, g(a, X), h(f(Y)))".asTerm.fresh.asInstanceOf[Term3[_,_,_,_]]
      f.arg2.asInstanceOf[Term2[_,_,_]].arg2.equals(f.arg1) must beTrue
      f.arg1.asInstanceOf[Var[_]].name must not be equalTo("X")
      val ny = f.arg3.asInstanceOf[Term1[_,_]].arg1.asInstanceOf[Term1[_,_]].arg1.asInstanceOf[Var[_]]
      f.arg1.equals(ny) must beFalse
      ny.name must not be equalTo("X")
      ny.name must not be equalTo("Y")
    }
    "create fresh varnames for longer var names" in new data {
      val varsBefore = variableStore.allVariables.size
      val name = "SomethingLongerThanUsualForAVariableName"
      name.asTerm.fresh.asInstanceOf[Var[_]].name must not be equalTo(name)
      variableStore.allVariables.size must be equalTo(varsBefore+2)
    }
  }
}