package scala.logic.state

import scala.logic.Term
import scala.logic.disjoint.DisjointSets
import scala.logic.Var

/**
 * A simple state, which holds a list of terms.
 *  
 * @author Frank Raiser
 */
case class TermState(
    val terms : List[Term], 
    val variableBindings : State.VariableBinding = Map.empty,
    val disjointSets : DisjointSets[Var] = new DisjointSets[Var](Map.empty)) extends State { 
  
  def bind(variable : Var, term : Term) : State = term match {
    case v : Var => TermState(terms, variableBindings, disjointSets.union(variable, v))
    case _ => TermState(terms, variableBindings + (variable -> term), disjointSets)
  }
  
  def addVariables(variables : Seq[Var]) =
    TermState(terms, variableBindings, disjointSets ++ variables)
    
}