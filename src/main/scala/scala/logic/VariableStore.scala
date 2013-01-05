package scala.logic

import scala.logic.disjoint.DisjointSets
import scala.util.Random
import scala.reflect.runtime.universe._

/**
 * A variable store keeps track of a set of variables and its unifications.
 * @author Frank Raiser
 */
class VariableStore {
  
  private var variables : Map[String, (Var[Any], String)] = Map.empty
  
  private var disjointSets = new DisjointSets[Var[Any]](Map.empty)
  
  val RANDOM_SUFFIX_LENGTH = 7
  
  def getSetRepresentative(v : Var[Any]) : Option[Var[Any]] = disjointSets.find(v)
  
  def union(var1 : Var[Any], var2 : Var[Any]) : Var[Any] = synchronized {
    disjointSets = disjointSets union (var1, var2)
    getSetRepresentative(var1).getOrElse(
        throw new RuntimeException("Variable lost from disjoint set during union"))
  }
  
  def allVariables = variables.values
  
  private def getRandomSuffix = ("%0" + RANDOM_SUFFIX_LENGTH + "d").format(
      (math.abs(Random.nextInt) % math.pow(10, RANDOM_SUFFIX_LENGTH).toInt))
  
  def getFreshNameWithPrefix(prefix : String) = {
    var name : String = prefix + getRandomSuffix
    while (variables.contains(name))
      name = prefix + getRandomSuffix
    name
  }
  
  def register[T : TypeTag](regVar : Var[T]) = {
    require(disjointSets.find(regVar.asInstanceOf[Var[Any]]) == None, 
        "Variable already registered in variable store.")
    disjointSets = disjointSets add regVar.asInstanceOf[Var[Any]]
    variables += (regVar.name -> (regVar.asInstanceOf[Var[Any]], typeOf[T].toString) )
  }
  
  def provideVar[T : TypeTag](name : String) : Var[T] = 
    variables.get(name) match {
      case Some((v, t)) =>
        if (t.equals(typeOf[T].toString)) v.asInstanceOf[Var[T]]
        else throw new RuntimeException("Type Error: variable " + name +
            " was registered as " + t +
            " and retrieved as " + typeOf[T].toString)
      case _ => new Var[T](name, this) // variable registers itself with us
    }
}