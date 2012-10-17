package scala.logic

/**
 * A constant is a term of arity 0, but we provide some support to
 * simply working with constants.
 * @author Frank Raiser
 */
case class Constant[T](val value : T) extends Term0[T] with Function0[T] {
  
  val symbol = value.toString
  
  def isGround = true
  
  override lazy val apply = value

  def =:=(other : Term[T]) = other match {
    case c : Constant[_] if c.value != value =>
      throw new UnificationException("Differing constants", this, other)
    case c : Constant[_] /* if c.value == value */ => 
      this
    case v : Var[_] =>
      // turn around, as the variable knows how to bind itself
      v =:= this
      this
    case _ =>
      throw new UnificationException("Constant and term cannot be unified", this, other)
  }
}