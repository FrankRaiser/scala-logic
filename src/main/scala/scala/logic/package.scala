package scala

package object logic {
  import scala.language.implicitConversions
  
  implicit def int2Constant(i : Int) = new Constant(i)
  
  implicit def string2TermBuilder(s : String) = new TermBuilder(s)
}