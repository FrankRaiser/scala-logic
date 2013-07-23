package scala.logic

import scala.logic.exception.UnificationException
import scala.logic.exception.MatchingException
import scala.logic.state.State

/**
 * A constant is a term of arity 0, but we provide some support to
 * simply working with constants.
 * @author Frank Raiser
 */
case class Constant(val value : String) extends Term {
  
  val arity = 0
  val symbol = value
  val arguments : List[Term] = Nil
  
  override def isGround(implicit context : State) = true
  
  override def occurs(v : Var) = false
    
  override val toString = value
  
  override def makeFreshTermWithVariables(
      freshVars : VariableSubstitution = Map.empty)(implicit context : State) : (Term, VariableSubstitution) =
        (this, freshVars)
}