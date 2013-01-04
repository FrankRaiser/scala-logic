package scala.logic

import org.specs2.mutable.Specification
import org.junit.runner.RunWith
import org.specs2.runner.JUnitRunner
import org.specs2.specification.Scope

@RunWith(classOf[JUnitRunner])
object TermEqualitySpec extends Specification {
  
  trait scope extends Scope {
    implicit val variableStore = new VariableStore
    val x = Var[Int]("X")
  }
  
  "Term equality" should {
    "be true for same constants" in {
      Constant[Int](3) should beEqualTo(Constant[Int](3))
    }
    "be false for different constants" in {
      Constant[Int](3) should not be equalTo(Constant[Int](4))
    }
    "be false for constant and other term" in new scope {
      Constant[Int](3) should not be equalTo(
          TermParser.parse("f(3)"))
      Constant[Int](3) should not be equalTo(
          TermParser.parse("f(3, 4)"))
      Constant[Int](3) should not be equalTo(
          TermParser.parse("f(3, 4, 5)"))
    }
    "be true for same variable" in new scope {
      x should be equalTo(Var[Int]("X"))
    }
    "be false for different non-unified variables" in new scope {
      x should not be equalTo(Var[Int]("Y"))
    }
    "be false for different unified variables" in new scope {
      val y = Var[Int]("Y")
      x =:= y
      x should not be equalTo(y)
    }
    "be false for variable and other term" in new scope {
      Var[Any]("Y") should not be equalTo(Constant[Any]("Y"))
      Var[Any]("Y") should not be equalTo(TermParser.parse("f(Y)"))
    }
    "be true for same 1-ary terms" in new scope {
      TermParser.parse("f(val1)") should be 
        equalTo(TermParser.parse("f(val1)"))
    }
    "be false for different 1-ary terms" in new scope {
      TermParser.parse("f(val2)") should not be 
        equalTo(TermParser.parse("f(val1)"))
    }
    "be false for 1-ary term and constant" in new scope {
      TermParser.parse("f(val1)") should not be
        equalTo(Constant[Any]("val1"))
    }
    "be true for same 2-ary terms" in new scope {
      TermParser.parse("f(val1, val2)") should be 
        equalTo(TermParser.parse("f(val1, val2)"))
    }
    "be false for different 2-ary terms" in new scope {
      TermParser.parse("f(val2, val1)") should not be 
        equalTo(TermParser.parse("f(val1, val2)"))
    }
    "be false for 2-ary term and constant" in new scope {
      TermParser.parse("f(val1, val2)") should not be
        equalTo(Constant[Any]("val1"))
    }
    "be true for same 3-ary terms" in new scope {
      TermParser.parse("f(val1, val2, val3)") should be 
        equalTo(TermParser.parse("f(val1, val2, val3)"))
    }
    "be false for different 3-ary terms" in new scope {
      TermParser.parse("f(val1, val2, val1)") should not be 
        equalTo(TermParser.parse("f(val1, val2, val3)"))
    }
    "be false for 3-ary term and constant" in new scope {
      TermParser.parse("f(val1, val2, val3)") should not be
        equalTo(Constant[Any]("val1"))
    }
  }
}