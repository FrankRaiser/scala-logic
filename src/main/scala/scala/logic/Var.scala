package scala.logic

/**
 * A typed logic variable, which registers itself with the provided store.
 */
class Var[T](val name : String)
    (implicit variableStore : VariableStore, mf : scala.reflect.Manifest[T]) 
    extends Term0[T] { 
  
  val symbol = name
  
  private var boundTerm : Option[Term[T]] = None
  
  def isBound = boundTerm.isDefined
  
  def isGround = boundTerm.map(_.isGround) == Some(true)
  
  def value : Option[T] = boundTerm match {
    case Some(t) if t.isInstanceOf[Function0[_]] => Some(t.asInstanceOf[Function0[T]]())
    case _ => None
  }
  
  def =:=(other : Term[T]) : Var[T] = {
    other match {
      case v if v.isInstanceOf[Var[_]] =>
        ???
      case t if boundTerm == None && other.occurs(this) =>
        throw new UnificationException("Variable must not occur in a term it is unified with", this, other)
      case t if boundTerm == None => // not yet bound, so bind the variable now
        boundTerm = Some(t)
      // this variable is already bound for the following cases
      case t : Term[_] =>
        // recursively unify the terms
        boundTerm.get =:= t
    }
    this // to allow for chaining unification
  }
  
  override def occurs[VT](variable : Var[VT]) = this == variable
  
  variableStore.register(this)(mf)
}

object Var {
  def apply[T](name : String)(implicit variableStore : VariableStore, mf : scala.reflect.Manifest[T]) = 
    variableStore.provideVar[T](name)(mf) 
}