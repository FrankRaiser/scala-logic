package scala.logic.state

import scala.logic._
import scala.logic.disjoint.DisjointSets
import scala.logic.exception.UnificationException

/**
 * A state that a program execution can be in. The state is 
 * also responsible to keep track of variable bindings, which
 * may change in-between states.
 * 
 * States should be immutable
 * 
 * @author Frank Raiser
 */
trait State { 
  
  /** a map of variables bound to their respective terms */
  def variableBindings : State.VariableBinding
  
  /** disjoint sets to keep track of variables bound to each other */
  def disjointSets : DisjointSets[Var[Any]]
  
  def boundTerm(variable : Var[Any]) : Option[Term[Any]] = 
    disjointSets.find(variable) flatMap variableBindings.get
    
  /** extends the state by an additional binding */
  def bind(variable : Var[Any], term : Term[Any]) : State
  
  /** adds the given variables to the state's DisjointSets */
  def addVariables(variables : Seq[Var[Any]]) : State
}

object State {
  type VariableBinding = Map[Var[Any], Term[Any]]
  
  trait Terms {
    type StateType = TermState
  }
}