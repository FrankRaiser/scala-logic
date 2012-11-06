package scala.logic

import org.specs2.mutable.Specification
import org.junit.runner.RunWith
import org.specs2.runner.JUnitRunner
import org.specs2.specification.Scope

@RunWith(classOf[JUnitRunner])
object TermParserSpec extends Specification {
  
  trait store extends Scope {
    implicit val variableStore = new VariableStore
    Var[Int]("X") =:= 3
  }
  
  "The term parser" should {
    "parse a constant" in new store {
      skipped("Implementation not finished yet")
      TermParser.parse("1") must be equalTo(Constant(2))
      TermParser.parse("a") must be equalTo(Constant(3))
      TermParser.parse("f(a)") must be equalTo(Constant(1))
    }
  }
}