package scala.logic

import org.specs2.mutable.Specification
import org.junit.runner.RunWith
import org.specs2.runner.JUnitRunner
import org.specs2.specification.Scope
import scala.logic.state.TermState
import scala.logic.unification.Unifier

@RunWith(classOf[JUnitRunner])
object TermSpec extends Specification {
  
  trait data extends Scope {
    val x = new Var("X")
    implicit val state = new TermState(Nil).addVariables(List(x))
    
    val unifiedState = Unifier.unify(x, "c".asTerm, state).toOption.get
  }
  
  "Terms" should {
    "support extracting their variables and" >> {
      "return empty list for constants" in {
        "a".asTerm.variables must beEmpty
      }
      "return variable itself in a list" in {
        val x = new Var("X")
        x.variables must be equalTo(Set(x))
      }
      "return empty list for nested terms without variables" in {
        "f(a)".asTerm.variables must beEmpty
        "f(a, g(b, h(c, d), e))".asTerm.variables must beEmpty
      }
      "return single variable from nested term" in {
        "f(a, g(X))".asTerm.variables must haveSize(1)
      }
      "return multiple distinct variables from nested terms" in {
        "f(X, g(Y), g(X, h(Z)))".asTerm.variables must haveSize(3)
      }
    }
    "support occurence checks and" >> {
      "find X in X" in new data {
        x.occurs(x) must beTrue
      }
      "not find X in c" in new data {
        "c".asTerm.occurs(x) must beFalse
      }
      "not find X in f(a,b)" in new data {
        "f(a,b)".asTerm.occurs(x) must beFalse
      }
      "find X in f(X)" in new data {
        "f(X)".asTerm.occurs(x) must beTrue
      }
    }
    "support ground checks for" >> {
      "a constant being ground" in new data {
        "c".asTerm.isGround must beTrue
      }
      "an unbound variable not being ground" in new data {
        x.isGround must beFalse
      }
      "a variable bound to a ground term being ground" in new data {
        x.isGround(unifiedState) must beTrue
        x.isGround must beFalse
      }
      "a term with ground arguments being ground" in new data {
        "f(a,b)".asTerm.isGround must beTrue
      }
      "a term with non-ground arguments being non-ground" in new data {
        "f(a, X)".asTerm.isGround must beFalse
      }
      "a term with ground variables being ground" in new data {
        "f(a, X)".asTerm.isGround(unifiedState) must beTrue
      }
    }
    "support substitution for" >> {
      "constants remaining unchanged" in new data {
        "c".asTerm.substituted(unifiedState) must be equalTo("c".asTerm)
      }
      "unbound variables remaining unchanged" in new data {
        x.substituted must be equalTo(x)
        "Y".asTerm.substituted(unifiedState) must be equalTo("Y".asTerm)
      }
      "ground terms remaining unchanged" in new data {
        "f(a,b,c)".asTerm.substituted(unifiedState) must be equalTo("f(a,b,c)".asTerm)
      }
      "bound variables being replaced by their term" in new data {
        x.substituted(unifiedState) must be equalTo("c".asTerm)
      }
      "terms with bound variables being replaced accordingly" in new data {
        "f(a, b, X)".asTerm.substituted(unifiedState) must be equalTo("f(a,b,c)".asTerm)
      }
    }
  }
}