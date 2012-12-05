package scala.logic

import org.specs2.mutable.Specification
import org.junit.runner.RunWith
import org.specs2.runner.JUnitRunner
import org.specs2.specification.Scope

@RunWith(classOf[JUnitRunner])
object TermParserSpec extends Specification {
  
  trait store extends Scope {
    implicit val variableStore = new VariableStore
  }
  
  "The term parser" should {
    "parse a constant int" in new store {
      //skipped("Implementation not finished yet")
      TermParser.parse("0") must be equalTo(Constant(0))
      TermParser.parse("1") must be equalTo(Constant(1))
      TermParser.parse("1234567890") must be equalTo(Constant(1234567890))
    }
    "parse a constant floating point number" in new store {
      TermParser.parse("2.3") must be equalTo(Constant(2.3))
      TermParser.parse(".3") must be equalTo(Constant(0.3))
      TermParser.parse("3.") must be equalTo(Constant(3.0))
      TermParser.parse("3e7") must be equalTo(Constant(3e7))
      TermParser.parse("123.45e6") must be equalTo(Constant(123.45e6))
    }
    "parse a constant string" in new store {
      TermParser.parse("a") must be equalTo(Constant("a"))
      TermParser.parse("abc") must be equalTo(Constant("abc"))
      TermParser.parse("abc123def") must be equalTo(Constant("abc123def"))
    }
    "parse unary terms" in new store {
      TermParser.parse("f(1)") must be equalTo(new Term1[Any, Int] {
        val symbol = "f"
        val arg1 = Constant(1)
      })
      TermParser.parse("f(abc)") must be equalTo(new Term1[Any, String] {
        val symbol = "f"
        val arg1 = Constant("abc")
      })
    }
    "parse nested unary terms" in new store { 
      TermParser.parse("f(g(1))") must be equalTo(new Term1[Any, Any] {
        val symbol = "f"
        val arg1 = new Term1[Any, Int] {
          val symbol = "g"
          val arg1 = Constant(1)
        }
      })
    }
    "parse binary terms" in new store {
      TermParser.parse("f(1, 3e5)") must be equalTo(new Term2[Any, Int, Double] {
        val symbol = "f"
        val arg1 = Constant(1)
        val arg2 = Constant(3e5)
      })
    }
    "parse tertiary terms" in new store {
      TermParser.parse("f(1, 2, 3)") must be equalTo(new Term3[Any, Int, Int, Int] {
        val symbol = "f"
        val arg1 = Constant(1)
        val arg2 = Constant(2)
        val arg3 = Constant(3)
      })
    }
    "parse variables" in new store {
      TermParser.parse("X") must be equalTo(Var[Any]("X"))
      TermParser.parse("SomeVar") must be equalTo(Var[Any]("SomeVar"))
      TermParser.parse("x") must be equalTo(Constant[Any]("x"))
      "f(3)".asTerm =:= Var("X")
      success
    }
  }
}