package unification

import scala.logic.state.State
import scala.logic.disjoint.DisjointSets
import scala.logic._
import scala.logic.exception.UnificationException
import scalaz._

/**
 * The Unifier object implements unification of arbitrary terms, based
 * on the given terms and their context in the form of existing variable
 * bindings and a disjointset structure.
 * 
 * @author Frank Raiser
 */
object Unifier {
  
  import Scalaz._
  
  type UnificationContext = State
  type UnificationResult = Validation[UnificationException[Any], UnificationContext]

  /**
   * Performs the actual unification within the given context and returns
   * a new context that results from the unification, or an exception explaining
   * why the unification was not possible.
   */
  def unify(term1 : Term[Any], term2 : Term[Any], context : UnificationContext) : UnificationResult = term1 match {
    case v : Var[Any] => unifyVar(v, term2, context)
    case c : Constant[Any] => unifyConstant(c, term2, context)
    case _ => unifyTerm(term1, term2, context)
  }
  
  private def unifyVar(var1 : Var[Any], term2 : Term[Any], context : UnificationContext) : UnificationResult =
    context.disjointSets.find(var1) match {
      case None => new UnificationException("Variable not found in DisjointSet of context", var1, term2).fail
      case Some(rootVar) => term2 match {
        case c : Constant[Any] => unifyVarConstant(rootVar, c, context)
        case v2 : Var[Any] if var1 == v2 => context.success
        case t : Term[Any] if t.occurs(rootVar) =>
          new UnificationException("Variable must not occur in bound term", var1, term2).fail
        case t : Term[Any] => context.bind(rootVar, t).success
        case _ => new UnificationException("Cannot unify variable with second term", var1, term2).fail
      }
    }
  
  private def unifyConstant(c : Constant[Any], term2 : Term[Any], context : UnificationContext) : UnificationResult = term2 match {
    case d : Constant[_] if c.value == d.value => context.success
    case d : Constant[_] => new UnificationException("Constants differ", c, d).fail
    case _ => new UnificationException("Cannot unify constant with second term", c, term2).fail
  }
  
  private def unifyTerm(term1 : Term[Any], term2 : Term[Any], context : UnificationContext) : UnificationResult = term2 match {
    case c : Constant[_] => new UnificationException("non-constant Term and constant cannot be unified", term1, term2).fail
    case v : Var[_] => unifyVar(v, term1, context)
    case t : Term[_] if term1.arity != t.arity =>
      new UnificationException("Terms with different arities cannot be unified", term1, term2).fail
    case t : Term[_] if term1.symbol != t.symbol=>
      new UnificationException("Terms with different symbols cannot be unified", term1, term2).fail
    case t : Term1[_,_] if term1.isInstanceOf[Term1[_,_]] =>
      unify(term1.asInstanceOf[Term1[Any,Any]].arg1, t.arg1.asInstanceOf[Term[Any]], context)
    case t : Term2[_,_,_] if term1.isInstanceOf[Term2[_,_,_]] =>
      for {
          c1 <- unify(term1.asInstanceOf[Term2[Any, Any, Any]].arg1, t.arg1.asInstanceOf[Term[Any]], context)
          c2 <- unify(term1.asInstanceOf[Term2[Any, Any, Any]].arg2, t.arg2.asInstanceOf[Term[Any]], c1) }
        yield c2
    case t : Term3[_,_,_,_] if term1.isInstanceOf[Term3[_,_,_,_]] =>
      for {
          c1 <- unify(term1.asInstanceOf[Term3[Any, Any, Any, Any]].arg1, t.arg1.asInstanceOf[Term[Any]], context)
          c2 <- unify(term1.asInstanceOf[Term3[Any, Any, Any, Any]].arg2, t.arg2.asInstanceOf[Term[Any]], c1)
          c3 <- unify(term1.asInstanceOf[Term3[Any, Any, Any, Any]].arg3, t.arg3.asInstanceOf[Term[Any]], c2) }
        yield c3
    case _ => context.success
  }
  
  private def unifyVarConstant(rootVar : Var[Any], c : Constant[Any], context : UnificationContext) : UnificationResult = 
      context.boundTerm(rootVar) match {
    case None => context.bind(rootVar, c).success
    case Some(t) => unifyConstant(c, t, context) // recursion into bound term
  }
}