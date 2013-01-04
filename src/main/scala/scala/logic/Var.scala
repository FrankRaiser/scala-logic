package scala.logic

import scala.logic.exception.UnificationException
import scala.logic.exception.MatchingException

/**
 * A typed logic variable, which registers itself with the provided store.
 * @author Frank Raiser
 */
class Var[T](val name : String)
    (implicit variableStore : VariableStore, mf : scala.reflect.Manifest[T]) 
    extends Term0[T] { 
  
  require(!name.isEmpty, "Variables must have a name")
  val symbol = name
  
  /** local term bound to this variable */
  private var boundTerm : Option[Term[T]] = None
  
  /** computes the actual term, in case the variable was unified with others and 
   * they hold the actual term (more precisely, *they* is the root of the disjoint sets) */
  def getTerm : Option[Term[T]] = 
    variableStore
      .getSetRepresentative(this.asInstanceOf[Var[Any]])
      .flatMap(_.asInstanceOf[Var[T]].boundTerm)
  
  def isBound = getTerm.isDefined
  
  def isGround = getTerm.map(_.isGround) == Some(true)
  
  /**
   * Equality check of variables: handle with extreme care!
   * Due to the need to access the disjoint sets datastructure in
   * order to determine the actual bound term, we cannot compare for
   * it with this method, as this would lead to an infinite recursion.
   * Instead, this only compares the variable symbol!
   */
  override def equals(other : Any) = other match {
    case v : Var[_] => 
      v.symbol == symbol
    case _ => false
  }
  
  def value : Option[T] = getTerm match {
    case Some(t) if t.isInstanceOf[Function0[_]] => Some(t.asInstanceOf[Function0[T]]())
    case _ => None
  }
  
  def =:=(other : Term[T]) : Var[T] = {
    val root1 = variableStore.getSetRepresentative(this.asInstanceOf[Var[Any]])
          .getOrElse(throw new RuntimeException("Internal error: variable root missing in variable store"))
    val root1T = root1.asInstanceOf[Var[T]]
    other match {
      case v : Var[_] =>
        val root2 = variableStore.getSetRepresentative(other.asInstanceOf[Var[Any]])
          .getOrElse(throw new RuntimeException("Internal error: variable root missing in variable store"))
        val term1 = root1.getTerm
        val term2 = root2.getTerm
        if (root1 == root2) {
          root1.asInstanceOf[Var[T]] 
        }
        else if (term1.isDefined && term2.isDefined) {
          term1.get =:= term2.get
          // if we made it to this point, then both bound terms
          // could be successfully unified, so we can unify the
          // variables themselves
          variableStore.union(root1, root2).asInstanceOf[Var[T]]
        } else if (term1.isDefined && !term2.isDefined) {
          if (term1.get.occurs(root2)) {
            throw new UnificationException("Variable must not occur in a term it is unified with", root1, root2)
          }
          root2.boundTerm = root1.boundTerm
          variableStore.union(root1, root2).asInstanceOf[Var[T]]
        } else if (!term1.isDefined && term2.isDefined) {
          if (term2.get.occurs(root1)) {
            throw new UnificationException("Variable must not occur in a term it is unified with", root1, root2)
          }
          root1.boundTerm = root2.boundTerm
          variableStore.union(root1, root2).asInstanceOf[Var[T]]
        } else { // both unbound
          val res = variableStore.union(root1, root2).asInstanceOf[Var[T]]
          if (res == root1) {
            root2.boundTerm = Some(root1)
          } else { 
            root1.boundTerm = Some(root2)
          }
          res
        }
      case t if root1.boundTerm == None && other.occurs(this) =>
        throw new UnificationException("Variable must not occur in a term it is unified with", this, other)
      case t if root1.boundTerm == None => // not yet bound, so bind the variable now
        root1T.boundTerm = Some(t)
        root1T
      // this variable is already bound for the following cases
      case t : Term[_] =>
        // recursively unify the terms
        root1T.boundTerm.get =:= t
        root1T
    }
  }
  
  def := (other : Term[T]) : Term[T] = {
    val root1 = variableStore.getSetRepresentative(this.asInstanceOf[Var[Any]])
          .getOrElse(throw new RuntimeException("Internal error: variable root missing in variable store"))
    val term1 = root1.getTerm
    if (term1.isDefined) {
      // Match to the actual term instead
      term1.get.asInstanceOf[Term[T]] := other
    } else other match {
      case v : Var[_] =>
        val root2 = variableStore.getSetRepresentative(other.asInstanceOf[Var[Any]])
          .getOrElse(throw new RuntimeException("Internal error: variable root missing in variable store"))
        val term2 = root2.getTerm
        if (term2.isDefined) {
          // Match to actual term instead
          this := term2.get.asInstanceOf[Term[T]]
        } else if (root1 == root2) {
          throw new MatchingException("Cannot match a variable to itself", this, other)
        } else {
          // unify the variables
          this =:= v
        }
      case t : Term[_] =>
        this =:= t
    }
  }
  
  def makeFreshTermWithVariables(
      freshVars : VariableSubstitution = Map.empty) : (Term[T], VariableSubstitution) = {
    if (freshVars.contains(this.asInstanceOf[Var[Any]])) {
      (freshVars.get(this.asInstanceOf[Var[Any]]).get.asInstanceOf[Term[T]], freshVars)
    } else {
      val newThis = new Var[T](variableStore.getFreshNameWithPrefix(getNamePrefix))(variableStore, mf)
      (newThis, freshVars + (this.asInstanceOf[Var[Any]] -> newThis.asInstanceOf[Var[Any]]))
    }
  }
    
  private def getNamePrefix = {
    if (name.size < variableStore.RANDOM_SUFFIX_LENGTH+1) name
    else try {
	  name.substring(name.size-variableStore.RANDOM_SUFFIX_LENGTH).toInt
	  name.substring(0, name.size-variableStore.RANDOM_SUFFIX_LENGTH)
	} catch {
	  case ex: NumberFormatException =>
	    name
	}
  }
  
  override def occurs[VT](variable : Var[VT]) = this == variable
  
  override def substituted : Term[T] = getTerm.getOrElse(this)
  
  variableStore.register(this)(mf)
}

object Var {
  def apply[T](name : String)(implicit variableStore : VariableStore, mf : scala.reflect.Manifest[T]) = 
    variableStore.provideVar[T](name)(mf) 
}