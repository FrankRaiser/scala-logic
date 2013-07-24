package scala.logic.unification

import org.specs2.mutable.Specification
import org.junit.runner.RunWith
import org.specs2.specification.Scope
import scala.logic._
import scala.logic.state.State
import org.specs2.runner.JUnitRunner

@RunWith(classOf[JUnitRunner])
trait UnificationSpec[T <: State] extends Specification {
  
  def emptyState : T
  
  trait data extends Scope {
    val x = new Var("X")
    val y = new Var("Y")
    val z = new Var("Y")
    val a = new Constant("a")
    val b = new Constant("b")
    
    def emptyStateX = emptyState.addVariables(List(x))
    def emptyStateXY = emptyState.addVariables(List(x,y))
    def emptyStateAllVars = emptyState.addVariables(List(x,y,z))
  }
  
  def noUnify(term1 : String, term2 : String, context : State = emptyState) = {
    Unifier.unify(term1.asTerm, term2.asTerm, context).toOption must beNone and
    (Unifier.unify(term2.asTerm, term1.asTerm, context).toOption must beNone)
  }
  
  def noMatch(term1 : String, term2 : String, context : State = emptyState) =
    Unifier.matchTerms(term1.asTerm, term2.asTerm, context).toOption must beNone

  "Unification in state context" should {
    "support constants and" >> {
	  "unify x = a" in new data {
	    val res = Unifier.unify(x, a, emptyStateX)
	    res.toOption must beSome
	    res.toOption.flatMap(_.boundTerm(x)) must be equalTo(Some(a))
	  }
	  
	  "unify a = a without changing state" in new data {
	    val res = Unifier.unify(a, a, emptyState)
	    res.toOption must beSome
	    res.toOption must be equalTo(Some(emptyState))
	  }
	  "not unify a = b" in new data {
	    noUnify("a", "b")
	  }
	  "not unify a = f(b)" in new data {
	    noUnify("a", "f(b)")
	  }
	  "match a = a" in new data {
	    Unifier.matchTerms(a, a, emptyState).toOption must beSome
	  }
	  "not match a = b" in new data {
	    noMatch("a", "b")
	  }
	  "match x = a" in new data {
	    val res = Unifier.matchTerms(x, a, emptyStateX)
	    res.toOption must beSome
	    res.toOption.flatMap(_.boundTerm(x)) must be equalTo(Some(a))
	  }
	  "not match a = f(b)" in new data {
	    noMatch("a", "f(b)")
	  }
    }
    "support terms of arity 1 and" >> {
      "unify f(a) = f(a) without changing state" in new data {
        val res = Unifier.unify("f(a)".asTerm, "f(a)".asTerm, emptyState)
        res.toOption must beSome
        res.toOption must be equalTo(Some(emptyState))
      }
      "not unify f(a) = a" in new data {
        noUnify("f(a)", "a")
      }
      "not unify terms of different arity" in new data {
        noUnify("f(a)", "f(a,a)")
      }
      "not unify terms with different symbols" in new data {
        noUnify("f(a)", "g(a)")
      }
      "not unify f(a) and f(b)" in new data {
        noUnify("f(a)", "f(b)")
      }
      "unify f(a) = X for unbound X" in new data {
        val res = Unifier.unify("f(a)".asTerm, x, emptyStateX)
        res.toOption must beSome
        res.toOption.flatMap(_.boundTerm(x)) must be equalTo(Some("f(a)".asTerm))
      }
      "unify a = X for unknown X" in new data {
        val res = Unifier.unify("a".asTerm, x, emptyState)
        res.toOption must beSome
        res.toOption.flatMap(_.boundTerm(x)) must be equalTo(Some("a".asTerm))
      }
      "unify a = X for X bound to a" in new data {
        Unifier.unify("a".asTerm, x, emptyStateX.bind(x, "a".asTerm)).toOption must beSome
      }
      "not unify a = X for X bound to b" in new data {
        noUnify("a", "X", emptyStateX.bind(x, "b".asTerm))
      }
      "match f(a) and f(a) without changing state" in new data {
        val res = Unifier.matchTerms("f(a)".asTerm, "f(a)".asTerm, emptyState)
        res.toOption must beSome
        res.toOption must be equalTo(Some(emptyState))
      }
      "not match f(a) and a" in {
        noMatch("f(a)", "a")
      }
      "not match terms of different arity" in {
        noMatch("f(a)", "f(a,a)")
      }
      "not match terms with different symbols" in {
        noMatch("f(a)", "g(a)")
      }
      "not match f(a) and f(b)" in {
        noMatch("f(a)", "f(b)")
      }
      "not match f(a) and X for unbound X" in new data{
        noMatch("f(a)", "X", emptyStateX)
      }
      "match f(a) and X for X=f(a)" in new data {
        val res = Unifier.unify(x, "f(a)".asTerm, emptyStateX)
        res.toOption must beSome
        Unifier.matchTerms("f(a)".asTerm, x, res.toOption.get).toOption must beSome
      }
      "unify f(X) and f(a) and bind X=a" in {
        val res = Unifier.unify("f(X)".asTerm, "f(a)".asTerm, emptyState)
        res.toOption must beSome
        res.toOption.get.boundTerm(new Var("X")) must be equalTo(Some("a".asTerm))
      }
      "match f(X) and f(a) and bind X=a" in {
        val res = Unifier.matchTerms("f(X)".asTerm, "f(a)".asTerm, emptyState)
        res.toOption must beSome
        res.toOption.get.boundTerm(new Var("X")) must be equalTo(Some("a".asTerm))
      }
    }
    "support terms of arity 2 and" >> {
      "unify f(a,b) = f(a,b) without changing state" in new data {
        val res = Unifier.unify("f(a,b)".asTerm, "f(a,b)".asTerm, emptyState)
        res.toOption must beSome
        res.toOption must be equalTo(Some(emptyState))
      }
      "not unify f(a,a) = a" in new data {
        noUnify("f(a,a)", "a")
      }
      "not unify f(a,a) = f(a)" in new data {
        noUnify("f(a,a)", "f(a)")
      }
      "not unify f(a,a) and f({a,b})" in new data {
        noUnify("f(a,a)", "f(a,b)")
        noUnify("f(a,a)", "f(b,a)")
      }
      "unify f(a,b) = X for unbound X" in new data {
        val res = Unifier.unify("f(a,b)".asTerm, x, emptyStateX)
        res.toOption must beSome
        res.toOption.flatMap(_.boundTerm(x)) must be equalTo(Some("f(a,b)".asTerm))
      }
    }
    "support terms of arity 3 and" >> {
      "unify f(a,b,c) = f(a,b,c) without changing state" in new data {
        val res = Unifier.unify("f(a,b,c)".asTerm, "f(a,b,c)".asTerm, emptyState)
        res.toOption must beSome
        res.toOption must be equalTo(Some(emptyState))
      }
      "not unify f(a,a,a) = a" in new data {
        noUnify("f(a,a,a)", "a")
      }
      "not unify f(a,a,a) = f(a)" in new data {
        noUnify("f(a,a,a)", "f(a)")
      }
      "not unify f(a,a,a) = f(a,a)" in new data {
        noUnify("f(a,a,a)", "f(a,a)")
      }
      "not unify f(a,a,a) and f({a,b})" in new data {
        noUnify("f(a,a,a)", "f(a,a,b)")
        noUnify("f(a,a,a)", "f(a,b,a)")
        noUnify("f(a,a,a)", "f(b,a,a)")
      }
      "unify f(a,b,c) = X for unbound X" in new data {
        val res = Unifier.unify("f(a,b,c)".asTerm, x, emptyStateX)
        res.toOption must beSome
        res.toOption.flatMap(_.boundTerm(x)) must be equalTo(Some("f(a,b,c)".asTerm))
      }
    }
    "support variables and" >> {
      "unify X = X without changing state or binding X" in new data {
        emptyStateX.boundTerm(x) must beNone
        val res = Unifier.unify(x, x, emptyStateX)
        res.toOption must beSome
        res.toOption must be equalTo(Some(emptyStateX))
        res.toOption.flatMap(_.boundTerm(x)) must beNone
      }
      "bind X = a" in new data {
        val res = Unifier.unify(x, a, emptyStateX)
        res.toOption must beSome
        res.toOption.flatMap(_.boundTerm(x)) must be equalTo(Some(a))
      }
      "bind a = X" in new data {
        val res = Unifier.unify(a, x, emptyStateX)
        res.toOption must beSome
        res.toOption.flatMap(_.boundTerm(x)) must be equalTo(Some(a))
      }
      "not unify X multiple times with different constants" in new data {
        val res = for {
            c1 <- Unifier.unify(x, a, emptyStateX)
            c2 <- Unifier.unify(x, b, c1) }
          yield c2
        res.toOption must beNone
      }
      "unify X = Y, but leave them unbound" in new data {
        val res = Unifier.unify(x, y, emptyStateXY)
        res.toOption must beSome
        val context = res.toOption.get
        context.disjointSets.find(x) must be equalTo(context.disjointSets.find(y))
        context.boundTerm(x) must beNone
        context.boundTerm(y) must beNone
      }
      "match X = Y for unbound X and Y" in new data {
        Unifier.matchTerms(x, y, emptyStateXY).toOption must beSome
      }
      "match X = Y for unbound X and Y=a" in new data {
        val res = Unifier.unify(y, a, emptyStateXY)
        res.toOption must beSome
        Unifier.matchTerms(x, y, res.toOption.get).toOption must beSome
      }
      "match X = a" in new data {
        Unifier.matchTerms(x, a, emptyStateX).toOption must beSome
      }
      "not match a = X" in new data {
        noMatch("a", "X", emptyStateX)
      }
      "match a = X if X bound to a" in new data {
        Unifier.matchTerms(a, x, emptyStateX.bind(x, "a".asTerm)).toOption must beSome
      }
      "match f(X,X) and f(a, a)" in new data {
        Unifier.matchTerms("f(X,X)".asTerm, "f(a, a)".asTerm, emptyStateX).toOption must beSome
      }
      "not match f(X,X) and f(a,b)" in new data {
        noMatch("f(X,X)", "f(a,b)", emptyStateX)
      }
    }
    "support occur check and " >> {
      "not unify X = f(X)" in new data {
        noUnify("X", "f(X)", emptyStateX)
      }
      "not unify X = f(a, X)" in new data {
        noUnify("X", "f(a, X)", emptyStateX)
      }
      "not unify X = f(a, g(X))" in new data {
        noUnify("X", "f(a, g(X))", emptyStateX)
      }
      "not unify X = f(a, g(h(b, X), c))" in new data {
        noUnify("X", "f(a, g(h(b, X), c))", emptyStateX)
      }
      "not match X and f(X)" in new data {
        noMatch("X", "f(X)", emptyStateX)
      }
    }
    "support keeping track of variable bindings and" >> {
      "keep track of bound term over multiple variables" in new data {
        val res = for {
          c1 <- Unifier.unify(x, y, emptyStateAllVars)
          c2 <- Unifier.unify(y, z, c1)
          c3 <- Unifier.unify(z, a, c2) } yield c3
        res.toOption must beSome
        val context = res.toOption.get
        context.boundTerm(z) must be equalTo(Some(a))
        context.boundTerm(x) must be equalTo(Some(a))
        context.boundTerm(y) must be equalTo(Some(a))
      } 
    }
    "support readme example" in new data {
      val term1 = "f(a, g(X), Y)".asTerm
      val term2 = "f(a, Y, g(a))".asTerm
      val res = Unifier.unify(term1, term2, emptyStateXY)
      res.toOption must beSome
      val stateAfter = res.toOption.get
      stateAfter.boundTerm(new Var("Y")).get.substituted(stateAfter) must be equalTo("g(a)".asTerm)
      stateAfter.boundTerm(new Var("X")) must be equalTo(Some("a".asTerm))
    }
  }
}