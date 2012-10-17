package scala.logic

import org.specs2.mutable.Specification
import org.junit.runner.RunWith
import org.specs2.runner.JUnitRunner
import org.specs2.specification.Scope

@RunWith(classOf[JUnitRunner])
object UnificationSpec extends Specification {
  // This spec tests some more complicated overall cases,
  // which correspond to the examples given on
  // http://en.wikipedia.org/wiki/Unification_(computer_science)
    
  trait data extends Scope {
    implicit val variableStore = new VariableStore
    val x = Var[Char]("X")
    val y = Var[Char]("Y")
    val z = Var[Char]("Z")
    val a = Constant('a')
    val b = Constant('b')
    // f(a, X)
    val fax = new Term2[Char, Char, Char]() {
      val symbol = "f"
      val arg1 = a
      val arg2 = x
    }
    // f(a, b)
    val fab = new Term2[Char, Char, Char]() {
      val symbol ="f"
      val arg1 = a
      val arg2 = b
    }
    // f(a)
    val fa = new Term1[Char, Char]() {
      val symbol = "f"
      val arg1 = a
    }
    // f(b)
    val fb = new Term1[Char, Char]() {
      val symbol = "f"
      val arg1 = b
    }
    // f(x)
    val fx = new Term1[Char, Char]() {
      val symbol = "f"
      val arg1 = x
    }
    // f(y)
    val fy = new Term1[Char, Char]() {
      val symbol = "f"
      val arg1 = y
    }
    // g(y)
    val gy = new Term1[Char, Char]() {
      val symbol = "g"
      val arg1 = y
    }
    // f(y,z)
    val fyz = new Term2[Char, Char, Char]() {
      val symbol = "f"
      val arg1 = y
      val arg2 = z
    }
    // f(g(x))
    val fgx = new Term1[Char, Char]() {
      val symbol = "f"
      val arg1 = new Term1[Char, Char]() {
        val symbol = "g"
        val arg1 = x
      }
    }
    // f(g(x), x)
    val fgxx = new Term2[Char, Char, Char]() {
      val symbol = "f"
      val arg1 = new Term1[Char, Char]() {
        val symbol = "g"
        val arg1 = x
      }
      val arg2 = x
    }
    // f(y, a)
    val fya = new Term2[Char, Char, Char]() {
      val symbol = "f"
      val arg1 = y
      val arg2 = a
    }
    // f(y, b)
    val fyb = new Term2[Char, Char, Char]() {
      val symbol = "f"
      val arg1 = y
      val arg2 = b
    }
  }
  
  def notBeUnifiable = throwA(new UnificationException("", null, null))
  def beUnifiable = throwA[Throwable].not

  "Unification support" should {
    "succeed for a=a" in new data {
      a =:= a must beUnifiable
    }
    "fail for a=b" in new data {
      a =:= b must notBeUnifiable
    }
    "succeed for x=x" in new data {
      x =:= x must beUnifiable
    }
    "succeed for a=x" in new data {
      a =:= x must beUnifiable
      x.getTerm must be equalTo(Some(Constant('a')))
    }
    "succeed for x=y" in new data {
      x =:= y must beUnifiable
    }
    "succeed for f(a, x) = f(a, b)" in new data {
      fax =:= fab must beUnifiable
      x.getTerm must be equalTo(Some(Constant('b')))
    }
    "fail for f(a) = f(b)" in new data {
      fa =:= fb must notBeUnifiable
    }
    "succeed for f(x) = f(y)" in new data {
      fx =:= fy must beUnifiable
    }
    "fail for f(x) = g(y)" in new data {
      fx =:= gy must notBeUnifiable
    }
    "fail for f(x) = f(y,z)" in new data {
      fx =:= fyz must notBeUnifiable
    }
    "succeed for f(g(x)) = f(y)" in new data {
      fgx =:= fy must beUnifiable
      y.getTerm must beSome
      y.getTerm.get must beAnInstanceOf[Term1[_,_]]
      val t = y.getTerm.get.asInstanceOf[Term1[Char, Char]]
      t.symbol must be equalTo("g")
      t.arg1 must be equalTo(x)
    }
    "succeed for f(g(x), x) = f(y, a)" in new data {
      fgxx =:= fya must beUnifiable
      x.getTerm must be equalTo(Some(Constant('a')))
      y.getTerm must beSome
      y.getTerm.get must beAnInstanceOf[Term1[_,_]]
      val t = y.getTerm.get.asInstanceOf[Term1[Char, Char]]
      t.symbol must be equalTo("g")
      t.arg1.substituted must be equalTo(Constant('a'))
    }
    
    // additional examples:
    "succeed for f(a, x) = f(y, b)" in new data {
      fax =:= fyb must beUnifiable
      x.getTerm must be equalTo(Some(Constant('b')))
      y.getTerm must be equalTo(Some(Constant('a')))
    }
  }
}