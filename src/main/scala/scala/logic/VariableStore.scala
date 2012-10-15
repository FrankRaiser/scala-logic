package scala.logic

/**
 * A variable store keeps track of a set of variables and its unifications.
 */
class VariableStore {
  
  private var variables : Map[String, Var[Any]] = Map.empty
  
  def allVariables = variables.values
  
  def register[T](regVar : Var[T]) = variables += (regVar.name -> regVar.asInstanceOf[Var[Any]])
  
  def provideVar[T](name : String) : Var[T] = variables.get(name) match {
    case Some(v) => v.asInstanceOf[Var[T]]
    case _ => new Var[T](name)(this) // variable registers itself with us
  }
}