package scala.logic

import scala.logic.exception.UnificationException
import scala.logic.exception.MatchingException
import scala.reflect.runtime.universe._
import scala.logic.state.State

/**
 * A typed logic variable, which registers itself with the provided store.
 * @author Frank Raiser
 */
class Var(val name : String) extends Term { 
  
  require(!name.isEmpty, "Variables must have a name")
  val symbol = name
  val arity = 0
  val arguments = Nil
  
  /** computes the actual term, in case the variable was unified with others and 
   * they hold the actual term (more precisely, *they* is the root of the disjoint sets) */
  def getTerm(implicit state : State) : Option[Term] =
    state.boundTerm(this)
  
  override def isGround(implicit state : State) = getTerm(state).map(_.isGround) == Some(true)
  
  override def variables = Set(this)
  
  /**
   * Equality check of variables: handle with extreme care!
   * Due to the need to access the disjoint sets datastructure in
   * order to determine the actual bound term, we cannot compare for
   * it with this method, as this would lead to an infinite recursion.
   * Instead, this only compares the variable symbol!
   */
  override def equals(other : Any) = other match {
    case v : Var => v.symbol == symbol
    case _ => false // covered, but not detected by scct due to a bug :(
  }
  
  override def makeFreshTermWithVariables(
      freshVars : VariableSubstitution = Map.empty)(implicit context : State) : (Term, VariableSubstitution) = 
        freshVars.get(this) match {
    case Some(mappedVar) => (mappedVar, freshVars)
    case _ =>
      val newThis = new Var(context.getFreshNameWithPrefix(getNamePrefix))
      (newThis, freshVars + (this -> newThis))
    }
  
  private def getNamePrefix = {
    if (name.size < State.RANDOM_SUFFIX_LENGTH+1) name
    else try {
	  name.substring(name.size-State.RANDOM_SUFFIX_LENGTH).toInt
	  name.substring(0, name.size-State.RANDOM_SUFFIX_LENGTH)
	} catch {
	  case ex: NumberFormatException =>
	    name
	}
  }
  
  override def occurs(variable : Var) = this == variable
  
  override def substituted(implicit context : State) : Term = getTerm(context).getOrElse(this)
  
  override def hashCode() = {
    val prime = 41
    prime + name.hashCode
  }
}