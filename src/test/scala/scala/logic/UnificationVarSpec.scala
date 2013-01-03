package scala.logic

import org.specs2.mutable.Specification
import org.junit.runner.RunWith
import org.specs2.runner.JUnitRunner
import org.specs2.specification.Scope
import scala.logic.exception.UnificationException

@RunWith(classOf[JUnitRunner])
object UnificationVarSpec extends Specification {
  
  trait store extends Scope {
    implicit val variableStore = new VariableStore
  }
  
  trait sampleData extends store {
    val x = Var[Int]("X")
  }
  
  trait sampleDataBound extends sampleData {
    val y = Var[Int]("Y")
    x =:= y must beUnifiable
  }
  
  def notBeUnifiable = throwA[Exception].like { case ue : UnificationException[_] => 1 === 1 }
  def beUnifiable = throwA[Throwable].not

  "A logic variable" should {
    "not be bound at creation" in new store {
      Var[Int]("X").value must beNone
    }
    "be unifiable with itself" in new store {
      Var[Int]("X") =:= Var("X") must beUnifiable
    }
    "be assigned to a store on creation" in new sampleData {
      variableStore.allVariables.map(_._1) must contain(x)
    }
    "bind to a constant and evaluate to it" in new sampleData {
      (x =:= 1).isBound must beTrue
      x.value must be equalTo(Some(1))
    }
    "not be unifiable with different constants" in new sampleData {
      x =:= 3 must beUnifiable
      x =:= 4 must notBeUnifiable
    }
    "be unifiable in a symmetric way with itself" in new sampleData {
      x =:= 3 must beUnifiable
      3 =:= x must beUnifiable
    }
    "be unifiable to another variable and still evaluate to None" in new sampleDataBound {
      x.value must beNone
    }
    "perform occur check during unification" in new sampleData {
      val fx = new Term1[Int, Int] {
        val symbol = "f"
        val arg1 = Var[Int]("X")
      }
      x =:= fx must notBeUnifiable
    }
    "keep track of bound term over multiple variables" in new sampleDataBound {
      val dest = Var[Int]("Dest")
      Var[Int]("Y") =:= Var[Int]("Z") =:= dest =:= 3 must beUnifiable
      x.isBound must beTrue
      x.value must be equalTo(Some(3))
    }
    
    "be non-ground if not bound" in new sampleData {
      x.isGround must beFalse
    }
    "be non-ground if bound to non-ground variable" in new sampleDataBound {
      x.isGround must beFalse
      y.isGround must beFalse
    }
    "be ground if bound to a constant" in new sampleData {
      (x =:= 3).isGround must beTrue
    }
    "be ground if bound to a ground variable" in new sampleDataBound {
      y =:= 3 must beUnifiable
      x.isGround must beTrue
    }
    "be non-ground if bound to a non-ground term" in new sampleData {
      x =:= new Term1[Int, Int] {
        val symbol = "f"
        val arg1 = Var[Int]("Y")
      }
      x.isGround must beFalse
    }
    "be ground if bound to a ground term" in new sampleData {
      x =:= new Term1[Int, Int] {
        val symbol = "f"
        val arg1 = Constant(3)
      }
      x.isGround must beTrue
    }
    "not unify for y = f(x) and x = y" in new sampleData {
      val y = Var[Int]("Y")
      y =:= new Term1[Int, Int] {
        val symbol = "f"
        val arg1 = x
      }
      x =:= y must notBeUnifiable
    }
    "not unify for y = f(x) and y = x" in new sampleData {
      val y = Var[Int]("Y")
      y =:= new Term1[Int, Int] {
        val symbol = "f"
        val arg1 = x
      }
      y =:= x must notBeUnifiable
    }
    "unify with a variable bound to same term" in new store {
      val x = Var[Any]("X")
      val y = Var[Any]("Y")
      x =:= TermParser.parse("f(val1, val2)")
      y =:= TermParser.parse("f(val1, val2)")
      x =:= y
    }
    "unify with a variable bound to a term" in new store {
      val x = Var[Any]("X")
      val y = Var[Any]("Y")
      y =:= TermParser.parse("f(val1, val2)")
      x =:= y
    }
    "be unifiable with a variable bound to a term" in new store {
      val x = Var[Any]("X")
      val y = Var[Any]("Y")
      y =:= TermParser.parse("f(val1, val2)")
      y =:= x
    }
    "unify with a unified unbound variable" in new store {
      val x = Var[Any]("X")
      val y = Var[Any]("Y")
      val z = Var[Any]("Z")
      y =:= z
      x =:= y
    }
  }
}