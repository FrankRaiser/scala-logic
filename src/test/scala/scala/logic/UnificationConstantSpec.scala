package scala.logic

import org.specs2.mutable.Specification
import org.junit.runner.RunWith
import org.specs2.runner.JUnitRunner
import org.specs2.specification.Scope

@RunWith(classOf[JUnitRunner])
object UnificationConstantSpec extends Specification {
  
  trait store extends Scope {
    implicit val variableStore = new VariableStore
  }
  
  def notBeUnifiable = throwA[Exception].like { case ue : UnificationException[_] => 1 === 1 }

  "A constant" should {
    "be unifiable with itself" in new store {
      3 =:= 3
    }
    "not be unifiable with a different constant" in new store {
      3 =:= 2 must notBeUnifiable
    }
    "not be unifiable with a different term" in new store {
      val t = new Term1[Int, Int] {
        val symbol = "f"
        val arg1 : Constant[Int] = 3
      }
      3 =:= t must notBeUnifiable
    }
    "bind to an unbound variable" in new store {
      3 =:= Var("X")
      Var[Int]("X").value must be equalTo(Some(3))
    }
  }
}