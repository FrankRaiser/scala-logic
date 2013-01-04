package scala.logic

import scala.logic.exception.UnificationException
import scala.logic.exception.MatchingException

/**
 * A constant is a term of arity 0, but we provide some support to
 * simply working with constants.
 * @author Frank Raiser
 */
case class Constant[T](val value : T) extends Term0[T] with Function0[T] {
  
  val symbol = value.toString
  
  def isGround = true
  
  override lazy val apply = value
  
  override val toString = value.toString
  
  override def equals(other : Any) = other match {
    case c : Constant[_] => value == c.value
    case _ => false
  }

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
  
  def :=(other : Term[T]) = other match {
    case c : Constant[_] if c.value != value =>
      throw new MatchingException("Differing constants", this, other)
    case c : Constant[_] =>
      this
    case v : Var[_] if !v.getTerm.isDefined =>
      throw new MatchingException("Constant cannot match unbound variable", this, other)
    case v : Var[_] if v.getTerm.equals(Some(this)) =>
      this
    case _ =>
      throw new MatchingException("Differing terms", this, other)
  }
  
  def makeFreshTermWithVariables(
      freshVars : VariableSubstitution = Map.empty) : (Term[T], VariableSubstitution) =
        (this, freshVars)
}