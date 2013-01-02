package scala.logic

import org.specs2.mutable.Specification
import org.junit.runner.RunWith
import org.specs2.runner.JUnitRunner
import org.specs2.specification.Scope

@RunWith(classOf[JUnitRunner])
object UnificationTermSpec extends Specification {
  
  trait terms extends Scope {
    implicit val variableStore = new VariableStore
    val f3 = new Term1[Int, Int] with Function0[Int] {
      val symbol = "f"
      val arg1 = Constant(3)
      override val apply = arg1.apply
    }
    val otherf3 = new Term1[Int, Int] {
      val symbol = "f"
      val arg1 = Constant(3)
    }
    
    val f23 = new Term2[Int, Int, Int] {
      val symbol = "f"
      val arg1 = Constant(2)
      val arg2 = Constant(3)
    }
    
    val g3 = new Term1[Int, Int] {
      val symbol = "g"
      val arg1 = Constant(3)
    }
    
    val h1 = new Term1[Int, Int] {
      val symbol = "h"
      val arg1 = f3
    }
    val h2 = new Term1[Int, Int] {
      val symbol = "h"
      val arg1 = g3
    }
    val g1f3h1 = new Term3[Int, Int, Int, Int] {
      val symbol = "g"
	  val arg1 = Constant(1)
	  val arg2 = f3
	  val arg3 = h1
    }
    val g1f3h2 = new Term3[Int, Int, Int, Int] {
      val symbol = "g"
	  val arg1 = Constant(1)
	  val arg2 = f3
	  val arg3 = h2
    }
  }
  
  def notBeUnifiable = throwA[Exception].like { case ue : UnificationException[_] => 1 === 1 } 

  "A term" should {
    "be unifiable with itself" in new terms {
      f3 =:= f3
    }
    "not be unifiable with a term of different arity" in new terms {
      f23 =:= f3 must notBeUnifiable
    }
    "not be unifiable with a term with different symbol" in new terms {
      f3 =:= g3 must notBeUnifiable
    }
    "be unifiable with a different term with same args" in new terms {
      f3 =:= otherf3
    }
    "not be unifiable if args differ" in new terms {
      h1 =:= h2 must notBeUnifiable
    }
    "bind to an unbound variable" in new terms {
      f23 =:= Var("X") must be equalTo(f23)
    }
    "support ternary terms such that" >> {
      "they unify with a variable" in new terms {
        g1f3h2 =:= Var("X")
      }
      "they unify with another ternary term" in new terms {
        g1f3h2 =:= g1f3h2
      }
      "they don't unify with a different ternary term" in new terms {
        g1f3h2 =:= g1f3h1 must notBeUnifiable
      }
    }
    "provide information on whether it is ground" >> {
      "for 2-ary ground terms" in new terms {
        f23.isGround must beTrue
      }
      "for 2-ary non-ground terms" in new terms {
        new Term2[Int, Int, Int] {
          val symbol = "g"
          val arg1 = f3
          val arg2 = Var[Int]("X")
        }.isGround must beFalse
      }
      "for 3-ary ground terms" in new terms {
        g1f3h2.isGround must beTrue
      }
      "for 3-ary non-ground terms" in new terms {
        new Term3[Int, Int, Int, Int] {
          val symbol = "f"
          val arg1 = f3
          val arg2 = Var[Int]("X")
          val arg3 = f3
        }.isGround must beFalse
      }
    }
    "provide substitutions of argument terms" >> {
      "for 1-ary terms" in new terms {
        val x = Var[Any]("X")
        x =:= Constant(3)
        TermParser.parse("f(X)").substituted =:= f3.asInstanceOf[Term[Any]]
      }
      "for 2-ary terms with arg1 non-ground" in new terms {
        val x = Var[Any]("X")
        x =:= Constant[Any]("val2")
        TermParser.parse("f(X, val3)").substituted =:= TermParser.parse("f(val2,val3)")
      }
      "for 2-ary terms with arg2 non-ground" in new terms {
        val x = Var[Any]("X")
        x =:= Constant[Any]("val3")
        TermParser.parse("f(val2, X)").substituted =:= TermParser.parse("f(val2, val3)")
      }
    }
  }
}