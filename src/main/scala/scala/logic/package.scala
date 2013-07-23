package scala

package object logic {
  import scala.language.implicitConversions

  implicit def string2TermBuilder(s : String) = new TermBuilder(s)
}