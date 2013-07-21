package scala.logic.state

import org.specs2.mutable.Specification
import org.junit.runner.RunWith
import org.specs2.runner.JUnitRunner
import org.specs2.specification.Scope
import scala.logic.exception.UnificationException
import scala.logic.VariableStore
import scala.logic._
import unification.Unifier
import org.specs2.matcher.MatchResult

@RunWith(classOf[JUnitRunner])
trait UnificationSpec[T <: State] extends Specification {
  
  def emptyState : T
  
  trait data extends Scope {
    implicit val variableStore = new VariableStore
    
    val x = Var[Any]("X")
    val y = Var[Any]("Y")
    val z = Var[Any]("Y")
    val a = Constant[Any]("a")
    val b = Constant[Any]("b")
    
    def emptyStateX = emptyState.addVariables(List(x))
    def emptyStateXY = emptyState.addVariables(List(x,y))
    def emptyStateAllVars = emptyState.addVariables(List(x,y,z))
  }
    
  def notBeUnifiable = throwA[Exception].like { case ue : UnificationException[_] => 1 === 1 }
  def beUnifiable = throwA[Throwable].not
  
  def noUnify(term1 : String, term2 : String)(implicit varStore : VariableStore) : MatchResult[Option[Unifier.UnificationContext]] = 
    noUnify(term1, term2, emptyState)(varStore)
  
  def noUnify(term1 : String, term2 : String, context : State)(implicit varStore : VariableStore) 
      : MatchResult[Option[Unifier.UnificationContext]] = {
    Unifier.unify(term1.asTerm, term2.asTerm, context).toOption must beNone
    Unifier.unify(term2.asTerm, term1.asTerm, context).toOption must beNone
  }

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
  }
}