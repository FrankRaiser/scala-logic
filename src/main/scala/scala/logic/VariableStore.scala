package scala.logic

import scala.logic.disjoint.DisjointSets

/**
 * A variable store keeps track of a set of variables and its unifications.
 * @author Frank Raiser
 */
class VariableStore {
  
  private var variables : Map[String, (Var[Any], String)] = Map.empty
  
  val disjointSets = new DisjointSets[Var[Any]](Nil)
  
  def allVariables = variables.values
  
  def register[T](regVar : Var[T])(implicit mf : scala.reflect.Manifest[T]) = {
    disjointSets add regVar.asInstanceOf[Var[Any]]
    variables += (regVar.name -> (regVar.asInstanceOf[Var[Any]], mf.toString) )
  }
  
  def provideVar[T](name : String)(implicit mf : scala.reflect.Manifest[T]) : Var[T] = 
    variables.get(name) match {
      case Some((v, t)) =>
        if (t.equals(mf.toString)) v.asInstanceOf[Var[T]]
        else throw new RuntimeException("Type Error: variable " + name +
            " was registered as " + t +
            " and retrieved as " + mf.toString)
      case _ => new Var[T](name)(this, mf) // variable registers itself with us
    }
}