package scala.logic

import org.specs2.mutable.Specification
import org.junit.runner.RunWith
import org.specs2.runner.JUnitRunner
import org.specs2.specification.Scope

@RunWith(classOf[JUnitRunner])
object TermParserSpec extends Specification {
  
  "The term parser" should {
    "fail to parse invalid terms" in {
      TermParser.parse("f(something, )") must throwA[Exception]
      TermParser.parse("f(something") must throwA[Exception]
      TermParser.parse("f((1)") must throwA[Exception]
      TermParser.parse(")1") must throwA[Exception]
      TermParser.parse("f()") must throwA[Exception]
    }
    "parse a constant int" in {
      //skipped("Implementation not finished yet")
      TermParser.parse("0") must be equalTo(new Constant("0"))
      TermParser.parse("1") must be equalTo(new Constant("1"))
      TermParser.parse("1234567890") must be equalTo(new Constant("1234567890"))
    }
    "parse a constant floating point number" in {
      TermParser.parse("2.3") must be equalTo(new Constant("2.3"))
      TermParser.parse(".3") must be equalTo(new Constant(".3"))
      TermParser.parse("3.") must be equalTo(new Constant("3."))
      TermParser.parse("3e7") must be equalTo(new Constant("3e7"))
      TermParser.parse("123.45e6") must be equalTo(new Constant("123.45e6"))
    }
    "parse a constant string" in {
      TermParser.parse("a") must be equalTo(new Constant("a"))
      TermParser.parse("abc") must be equalTo(new Constant("abc"))
      TermParser.parse("abc123def") must be equalTo(new Constant("abc123def"))
    }
    "parse unary terms" in {
      TermParser.parse("f(1)") must be equalTo(new Term {
        val symbol = "f"
        val arity = 1
        val arguments = List(new Constant("1"))
      })
      TermParser.parse("f(abc)") must be equalTo(new Term {
        val symbol = "f"
        val arity = 1
        val arguments = List(new Constant("abc"))
      })
    }
    "parse nested unary terms" in { 
      TermParser.parse("f(g(1))") must be equalTo(new Term {
        val symbol = "f"
        val arity = 1
        val arguments = List(new Term{
          val symbol = "g"
          val arity = 1
          val arguments = List(new Constant("1"))
        })
      })
    }
    "parse binary terms" in {
      TermParser.parse("f(1, 3e5)") must be equalTo(new Term{
        val symbol = "f"
        val arity = 2
        val arguments = List(
          new Constant("1"),
          new Constant("3e5"))
      })
    }
    "parse tertiary terms" in {
      TermParser.parse("f(1, 2, 3)") must be equalTo(new Term {
        val symbol = "f"
        val arity = 3
        val arguments = List(
          new Constant("1"),
          new Constant("2"),
          new Constant("3"))
      })
    }
    "parse variables" in {
      TermParser.parse("X") must be equalTo(new Var("X"))
      TermParser.parse("SomeVar") must be equalTo(new Var("SomeVar"))
      TermParser.parse("x") must be equalTo(new Constant("x"))
    }
  }
}