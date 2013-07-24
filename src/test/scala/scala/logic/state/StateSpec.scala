package scala.logic.state

import org.specs2.mutable.Specification
import org.junit.runner.RunWith
import org.specs2.runner.JUnitRunner
import org.specs2.specification.Scope
import scala.logic._
import scala.logic.unification.Unifier

@RunWith(classOf[JUnitRunner])
trait StateSpec[T <: State] extends Specification {
    
  def emptyState : T
  
  trait data extends Scope {
    val x = new Var("X")
    val fa = "f(a)".asTerm
    val fx = "f(X)".asTerm
    val ghabc = "g(h(a,b), c)".asTerm
    val state = emptyState ++ List(fa, fx, ghabc)
  }
  
  "A state implementation" should {
    "allow adding 0 terms" in {
      emptyState ++ List() must be equalTo(emptyState)
    }
    "allow adding a term" in {
      (emptyState ++ List("f(a)".asTerm)).size must be equalTo(1)
    }
    "allow adding mutiple distinct terms" in new data {
      state.size must be equalTo(3)
    }
    "allow adding multiples of same term" in {
      (emptyState ++ List("f(a)".asTerm, "f(a)".asTerm)).size must be equalTo(2)
    }
    "make variables of added terms available for binding" in {
      val x = new Var("X")
      val res = (emptyState ++ List("f(a)".asTerm, "f(X)".asTerm))
      res.boundTerm(x) must beNone
      val boundState = res.bind(x, "a".asTerm)
      boundState.boundTerm(x) must be equalTo(Some("a".asTerm))
    }
    "allow subtracting some terms" in new data {
      (state -- List(fa, fx)).size must be equalTo(1)
    }
    "allow clearing" in {
      emptyState.clear.size must be equalTo(0)
    }
    "keep variable bindings after clearing" in new data {
      val res = state.bind(x, "a".asTerm)
      res.clear.boundTerm(x) must be equalTo(Some("a".asTerm))
    }
    
    "retrieve a contained term for exact match" in new data {
      state.findTermsThatMatch(fa) must haveSize(1)
    }
    "retrieved contained terms for match involving var binding" in new data {
      val boundState = (emptyState ++ List("f(Y)".asTerm)).bind(new Var("Y"), "a".asTerm)
      boundState.findTermsThatMatch("f(a)".asTerm) must haveSize(1)
    }
    "not retrieve f(a) when matching f(y)" in new data {
      state.findTermsThatMatch("f(Y)".asTerm) must beEmpty
    }
    "support regression cases" >> {
      "find term f(X) for X=a to match f(a)" in {
        val fa = "f(a)".asTerm
        val fx = "f(X)".asTerm
        val x = new Var("X")
        val state = TermState(List(fx)).bind(x, "a".asTerm)
        state.findTermsThatMatch(fa) must not beEmpty
      }
    }
  }
}