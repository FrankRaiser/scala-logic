package scala.logic.state

import scala.logic.Term
import scala.logic.disjoint.DisjointSets
import scala.logic.Var
import scala.logic.unification.Unifier

/**
 * A simple state, which holds a list of terms.
 * 
 * @author Frank Raiser
 */
case class TermState(
    val terms : List[Term], 
    val variableBindings : State.VariableBinding = Map.empty,
    val disjointSets : DisjointSets[Var] = new DisjointSets[Var](Map.empty)) extends State { 
  
  def ++(otherTerms : Seq[Term]) = new TermState(
      terms ++ otherTerms,
      variableBindings,
      disjointSets ++ otherTerms.flatMap(_.variables)
      )
 
  def --(otherTerms : Seq[Term]) = new TermState(
      terms.filterNot(otherTerms.contains), variableBindings, disjointSets)
  
  def clear = new TermState(Nil, variableBindings, disjointSets)
  
  val size = terms.size
  
  def bind(variable : Var, term : Term) : State = disjointSets.find(variable) match {
    case Some(rootVar) =>  term match {
      case v : Var => TermState(terms, variableBindings, (disjointSets + v).union(rootVar, v))
      case _ => TermState(terms, variableBindings + (variable -> term), disjointSets)
    }
    case _ => term match {
      case v : Var => TermState(terms, variableBindings, (disjointSets ++ List(variable, v)).union(variable, v))
      case _ => TermState(terms, variableBindings + (variable -> term), disjointSets + variable) 
    }
  }
  
  def addVariables(variables : Seq[Var]) =
    TermState(terms, variableBindings, disjointSets ++ variables)
    
  def findTermsThatMatch(otherTerm : Term) : Stream[Term] =
    terms.toStream.filter(
        term => Unifier.matchTerms(otherTerm, term, this).toOption != None)
}