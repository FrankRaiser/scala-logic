package scala.logic

import org.specs2.mutable.Specification
import org.junit.runner.RunWith
import org.specs2.runner.JUnitRunner
import org.specs2.specification.Scope
import scala.logic.exception.UnificationException

@RunWith(classOf[JUnitRunner])
object MatchingSpec extends Specification {
  /*trait data extends Scope {
    implicit val variableStore = new VariableStore
    val x = Var[Any]("X")
    val y = Var[Any]("Y")
    val z = Var[Any]("Z")
    val a = Constant[Any]("a")
    val b = Constant[Any]("b")
    val c = Constant[Any]("c")
  }
  
  def notBeMatching = throwA[Exception].like { case ue : UnificationException[_] => 1 === 1 }

  "Matching support" should {
    "for constants" >> {
      "succeed for a:=a" in new data {
        a := a
      }
      "fail for a=b" in new data {
        (a := b) must notBeMatching
      }
      "fail for a:=X with unbound X" in new data {
        (a := x) must notBeMatching
      }
      "fail for a:=X with X bound to b" in new data {
        x =:= b
        (a := x) must notBeMatching
      }
      "succeed for a:=X with X bound to a" in new data {
        x =:= a
        a := x
      }
    }
    "for variables" >> {
      "succeed for X:=a with unbound X" in new data {
        x := a
      }
      "succeed for X:=Y with unbound X and Y" in new data {
        x := y
      }
      "succeed for X:=Y with Y bound to a" in new data {
        y =:= a
        x := y
      }
      "succeed for X:=a with X bound to a" in new data {
        x =:= a
        x := a
      }
      "fail for X:=a with X bound to b" in new data {
        x =:= b
        (x := a) must notBeMatching
      }
      "fail for same variable X:=X" in new data {
        (x := x) must notBeMatching 
      }
      "fail for same variable due to occur check" in new data {
        (x := TermParser.parse("f(X)")) must notBeMatching
        (x := TermParser.parse("f(a,X)")) must notBeMatching
        (x := TermParser.parse("f(X,a)")) must notBeMatching
        (x := TermParser.parse("f(a,b,X)")) must notBeMatching
        (x := TermParser.parse("f(a,X,c)")) must notBeMatching
        (x := TermParser.parse("f(X,b,c)")) must notBeMatching
        (x := TermParser.parse("f(a, b, f(c, f(X)))")) must notBeMatching
      }
      "succeed for X:=f(a) with unbound X" in new data {
        x := TermParser.parse("f(a)")
      }
      "succeed for X:=f(a) with X bound to f(a)" in new data {
        x =:= TermParser.parse("f(a)")
        x := TermParser.parse("f(a)")
      }
      "fail for X:=f(a) with X bound to g(a)" in new data {
        x =:= TermParser.parse("g(a)")
        (x := TermParser.parse("f(a)")) must notBeMatching
      }
      "fail for X:=f(a) with X bound to f(a,b)" in new data {
        x =:= TermParser.parse("f(a,b)")
        (x := TermParser.parse("f(a)")) must notBeMatching
      }
      "fail for X:=f(a) with X bound to f(b)" in new data {
        x =:= TermParser.parse("f(b)")
        (x := TermParser.parse("f(a)")) must notBeMatching
      }
    }
    "for 1-ary terms" >> {
      "succeed for f(a):=f(a)" in new data {
        TermParser.parse("f(a)") := TermParser.parse("f(a)")
      }
      "fail for f(a):=f(b)" in new data {
        (TermParser.parse("f(a)") := TermParser.parse("f(b)")) must notBeMatching
      }
      "fail for f(a):=X with unbound X" in new data {
        (TermParser.parse("f(a)") := x) must notBeMatching
      }
      "succeed for f(a):=X with X bound to f(a)" in new data {
        x =:= "f(a)".asTerm
        "f(a)".asTerm := x
      }
      "fail for f(a):=f(X) with unbound X" in new data {
        (TermParser.parse("f(a)") := TermParser.parse("f(X)")) must notBeMatching
      }
      "fail for f(a):=f(X) with X bound to b" in new data {
        x =:= b
        (TermParser.parse("f(a)") := TermParser.parse("f(X)")) must notBeMatching
      }
      "succeed for f(a):=f(X) with X bound to a" in new data {
        x =:= a
        TermParser.parse("f(a)") := TermParser.parse("f(X)")
      }
    }
    "for 2-ary terms" >> {
      "succeed for f(a,b):=f(a,b)" in new data {
        TermParser.parse("f(a,b)") := TermParser.parse("f(a,b)")
      }
      "fail for f(a,b):=f(b,a)" in new data {
        (TermParser.parse("f(a,b)") := TermParser.parse("f(b,a)")) must notBeMatching
      }
      "fail for f(a,b):=X with unbound X" in new data {
        (TermParser.parse("f(a,b)") := x) must notBeMatching
      }
      "succeed for f(a,b):=X with X bound to f(a,b)" in new data {
        x =:= "f(a,b)".asTerm
        "f(a,b)".asTerm := x
      }
      "fail for f(a,b):=f(X,b) with unbound X" in new data {
        (TermParser.parse("f(a,b)") := TermParser.parse("f(X,b)")) must notBeMatching
      }
      "fail for f(a,b):=f(X,b) with X bound to b" in new data {
        x =:= b
        (TermParser.parse("f(a,b)") := TermParser.parse("f(X,b)")) must notBeMatching
      }
      "succeed for f(a,b):=f(X,b) with X bound to a" in new data {
        x =:= a
        TermParser.parse("f(a,b)") := TermParser.parse("f(X,b)")
      }
    }
    "for 3-ary terms" >> {
      "succeed for f(a,b,c):=f(a,b,c)" in new data {
        TermParser.parse("f(a,b,c)") := TermParser.parse("f(a,b,c)")
      }
      "fail for f(a,b,c):=f(c,b,a)" in new data {
        (TermParser.parse("f(a,b,c)") := TermParser.parse("f(c,b,a)")) must notBeMatching
      }
      "fail for f(a,b,c):=X with unbound X" in new data {
        (TermParser.parse("f(a,b,c)") := x) must notBeMatching
      }
      "succeed for f(a,b,c):=X with X bound to f(a,b,c)" in new data {
        x =:= "f(a,b,c)".asTerm
        "f(a,b,c)".asTerm := x
      }
      "fail for f(a,b,c):=f(X,b,c) with unbound X" in new data {
        (TermParser.parse("f(a,b,c)") := TermParser.parse("f(X,b,c)")) must notBeMatching
      }
      "fail for f(a,b,c):=f(a,X,c) with X bound to a" in new data {
        x =:= a
        (TermParser.parse("f(a,b,c)") := TermParser.parse("f(a,X,c)")) must notBeMatching
      }
      "succeed for f(a,b,c):=f(a,b,X) with X bound to c" in new data {
        x =:= c
        TermParser.parse("f(a,b,c)") := TermParser.parse("f(a,b,X)")
      }
    }
  }*/
}