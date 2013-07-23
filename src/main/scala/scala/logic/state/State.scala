package scala.logic.state

import scala.logic._
import scala.logic.disjoint.DisjointSets
import scala.logic.exception.UnificationException
import scala.util.Random

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
  def disjointSets : DisjointSets[Var]
  
  def allVariables = disjointSets.nodes.keys
  
  def boundTerm(variable : Var) : Option[Term] = 
    disjointSets.find(variable) flatMap variableBindings.get
    
  /** extends the state by an additional binding */
  def bind(variable : Var, term : Term) : State
  
  /** adds the given variables to the state's DisjointSets */
  def addVariables(variables : Seq[Var]) : State
  
  def getFreshNameWithPrefix(prefix : String) = {
    def getRandomSuffix = ("%0" + State.RANDOM_SUFFIX_LENGTH + "d").format(
      (math.abs(Random.nextInt) % math.pow(10, State.RANDOM_SUFFIX_LENGTH).toInt))
      
    var name : String = ""
    val variables = allVariables.toList
    do {
      name = prefix + getRandomSuffix
    } 
    while (variables.contains(name))
      
    name
  }
}

object State {
  type VariableBinding = Map[Var, Term]
  
  val RANDOM_SUFFIX_LENGTH = 7
  
  trait Terms {
    type StateType = TermState
  }
}