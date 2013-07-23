package scala.logic

import org.specs2.mutable.Specification
import org.junit.runner.RunWith
import org.specs2.runner.JUnitRunner
import org.specs2.specification.Scope
import scala.logic.state.TermState
import scala.collection.immutable.HashSet

@RunWith(classOf[JUnitRunner])
object TermEqualitySpec extends Specification {
  
  trait scope extends Scope {
    val x = new Var("X")
  }
  
  "Term equality" should {
    "be true for same constants" in {
      new Constant("3") should beEqualTo(new Constant("3"))
    }
    "be false for different constants" in {
      new Constant("3") should not be equalTo(new Constant("4"))
    }
    "be false for constant and other term" in new scope {
      new Constant("3") should not be equalTo("f(3)".asTerm)
      new Constant("3") should not be equalTo("f(3, 4)".asTerm)
      new Constant("3") should not be equalTo("f(3, 4, 5)".asTerm)
      new Constant("3") should not be equalTo("X".asTerm)
    }
    "be true for same variable" in new scope {
      x should be equalTo(new Var("X"))
    }
    "be false for different non-unified variables" in new scope {
      x should not be equalTo(new Var("Y"))
    }
    "be false for variable and other term" in new scope {
      new Var("Y") should not be equalTo(new Constant("Y"))
      new Var("Y") should not be equalTo("f(Y)".asTerm)
    }
    "be true for same 1-ary terms" in new scope {
      "f(val1)".asTerm should be equalTo("f(val1)".asTerm)
    }
    "be false for different 1-ary terms" in new scope {
      "f(val2)".asTerm should not be equalTo("f(val1)".asTerm)
    }
    "be false for 1-ary term and constant" in new scope {
      "f(val1)".asTerm should not be equalTo(new Constant("val1"))
    }
    "be true for same 2-ary terms" in new scope {
      "f(val1, val2)".asTerm should be equalTo("f(val1, val2)".asTerm)
    }
    "be false for 2-ary term and constant" in new scope {
      "f(val1, val2)".asTerm should not be equalTo(new Constant("val1"))
    }
    "be true for same 3-ary terms" in new scope {
      "f(val1, val2, val3)".asTerm should be equalTo("f(val1, val2, val3)".asTerm)
    }
    "be false for different 3-ary terms" in new scope {
      "f(val1, val2, val1)".asTerm should not be equalTo("f(val1, val2, val3)".asTerm)
    }
    "be false for 3-ary term and constant" in new scope {
      "f(val1, val2, val3)".asTerm should not be equalTo(new Constant("val1"))
    }
    "hash constants to their value" in {
      HashSet() ++ List("a","b","c","a","b","c").map(_.asTerm) must haveSize(3)
    }
    "hash terms such that arguments are included" in {
      HashSet() ++ List("f(a)", "f(X)", "f(a)").map(_.asTerm) must haveSize(2)
    }
    "hash terms such that their symbols are included" in {
      HashSet() ++ List("f(a)", "g(a)", "f(b)", "g(b)").map(_.asTerm) must haveSize(4)
    }
  }
}