package scala.logic

import org.specs2.mutable.Specification
import org.junit.runner.RunWith
import org.specs2.runner.JUnitRunner
import org.specs2.specification.Scope
import scala.logic.exception.UnificationException

@RunWith(classOf[JUnitRunner])
object VariableStoreSpec extends Specification {
  
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
  
  def notBeUnifiable = throwA(new UnificationException("", null, null))
  def beUnifiable = throwA[Throwable].not

  "A variable store" should {
    "provide the same variable objects for the same names" in new sampleData {
      Var[Int]("X") must be equalTo(x)
    }
    "raise a runtime exception if retrieving a variable of wrong type" in new sampleData {
      variableStore.provideVar[String]("X") must throwA[Throwable]
      Var[String]("X") must throwA[Throwable]
    }
    "create fresh variable names by finding a random empty space" in {
      implicit val vs = new VariableStore {
        override val RANDOM_SUFFIX_LENGTH = 1
      }
      for (i <- 0 to 9 if i != 4) Var[Any]("X" + i)
      vs.allVariables.map(_._1.name) must containAllOf(List("X0", "X1", "X9"))
      // repeat several times to ensure the correct name is not guessed
      // immediately each time (probability < 10^10 now)
      for (i <- 1 to 10) {
        vs.getFreshNameWithPrefix("X") must be equalTo("X4") 
      }
    }
  }
}