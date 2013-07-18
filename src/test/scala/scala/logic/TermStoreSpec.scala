package scala.logic

import org.specs2.mutable.Specification
import org.junit.runner.RunWith
import org.specs2.runner.JUnitRunner
import org.specs2.specification.Scope
import scala.logic.exception.UnificationException

@RunWith(classOf[JUnitRunner])
object TermStoreSpec extends Specification {
  trait data extends Scope {
    implicit val variableStore = new VariableStore
    val x = Var[Any]("X")
    val y = Var[Any]("Y")
    val z = Var[Any]("Z")
    val a = Constant[Any]("a")
    val b = Constant[Any]("b")
    val c = Constant[Any]("c")
    val t = "f(a)".asTerm
    val fx = "f(X)".asTerm
    val fb = "f(b)".asTerm
    val terms = List(t, fx, fb)
    val termStore = new TermStore[Any]
    val fullStore = new TermStore[Any] ++ terms 
  }
  
  def notBeUnifiable = throwA[Exception].like { case ue : UnificationException[_] => 1 === 1 }
  def beUnifiable = throwA[Throwable].not

  "A term store" should {
    "support being empty" in {
      (new TermStore).isEmpty must beTrue
    }
    
    "store a term" in new data {
      val extendedTermStore = termStore + t
      extendedTermStore must not beEmpty
      
      extendedTermStore.terms must contain(t)
    }
    
    "store multiple terms" in new data {
      fullStore must not beEmpty
      
      fullStore.terms must containAllOf(terms)
    }
    
    "remove a term" in new data {
      val store = fullStore - t
      store must not beEmpty
      
      store.terms must not contain(t)
      store.terms must haveSize(terms.size - 1)
    }
    
    "remove multiple terms" in new data {
      val store = fullStore -- List(t, fx, a)
      
      store.terms must not contain(t)
      store.terms must not contain(fx)
      store.terms must haveSize(terms.size - 2)
    }
  }
}