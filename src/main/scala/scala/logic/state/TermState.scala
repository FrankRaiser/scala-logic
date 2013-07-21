package scala.logic.state

import scala.logic.TermStore
import scala.logic.Term
import scala.logic.disjoint.DisjointSets
import scala.logic.Var
import unification.Unifier
import scala.logic.exception.UnificationException

/**
 * A simple state, which holds a list of terms.
 *  
 * @author Frank Raiser
 */
case class TermState(
    val terms : List[Term[_]], 
    val variableBindings : State.VariableBinding = Map.empty,
    val disjointSets : DisjointSets[Var[Any]] = new DisjointSets[Var[Any]](Map.empty)) extends State { 
  
  def bind(variable : Var[Any], term : Term[Any]) : State = term match {
    case v : Var[Any] => TermState(terms, variableBindings, disjointSets.union(variable, v))
    case _ => TermState(terms, variableBindings + (variable -> term), disjointSets)
  }
  
  def addVariables(variables : Seq[Var[Any]]) =
    TermState(terms, variableBindings, disjointSets ++ variables)
    
}