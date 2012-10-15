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
  }
  
  def notBeUnifiable = throwA(new UnificationException("", null, null))

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
  }
}