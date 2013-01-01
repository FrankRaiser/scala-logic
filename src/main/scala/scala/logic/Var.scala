package scala.logic

/**
 * A typed logic variable, which registers itself with the provided store.
 * @author Frank Raiser
 */
class Var[T](val name : String)
    (implicit variableStore : VariableStore, mf : scala.reflect.Manifest[T]) 
    extends Term0[T] { 
  
  val symbol = name
  
  /** local term bound to this variable */
  private var boundTerm : Option[Term[T]] = None
  
  /** computes the actual term, in case the variable was unified with others and 
   * they hold the actual term (more precisely, *they* is the root of the disjoint sets) */
  def getTerm : Option[Term[T]] = 
    variableStore
      .getSetRepresentative(this.asInstanceOf[Var[Any]])
      .flatMap(_.asInstanceOf[Var[T]].boundTerm)
  
  def isBound = getTerm.isDefined
  
  def isGround = getTerm.map(_.isGround) == Some(true)
  
  /**
   * Equality check of variables: handle with extreme care!
   * Due to the need to access the disjoint sets datastructure in
   * order to determine the actual bound term, we cannot compare for
   * it with this method, as this would lead to an infinite recursion.
   * Instead, this only compares the variable symbol!
   */
  override def equals(other : Any) = other match {
    case v : Var[_] => 
      v.symbol == symbol
    case _ => false
  }
  
  def value : Option[T] = getTerm match {
    case Some(t) if t.isInstanceOf[Function0[_]] => Some(t.asInstanceOf[Function0[T]]())
    case _ => None
  }
  
  def =:=(other : Term[T]) : Var[T] = {
    val root1 = variableStore.getSetRepresentative(this.asInstanceOf[Var[Any]])
          .getOrElse(throw new RuntimeException("Internal error: variable root missing in variable store"))
    val root1T = root1.asInstanceOf[Var[T]]
    other match {
      case v if v.isInstanceOf[Var[_]] =>
        val root2 = variableStore.getSetRepresentative(other.asInstanceOf[Var[Any]])
          .getOrElse(throw new RuntimeException("Internal error: variable root missing in variable store"))
        // find representatives of disjoint set
        if (root1 == root2) {
          root1.asInstanceOf[Var[T]] 
        }
        else if (root1.isBound && root2.isBound) {
          root1.boundTerm.get =:= root2.boundTerm.get
          // if we made it to this point, then both bound terms
          // could be successfully unified, so we can unify the
          // variables themselves
          variableStore.union(root1, root2).asInstanceOf[Var[T]]
        } else if (root1.isBound && !root2.isBound) {
          if (root1.occurs(root2)) {
            throw new UnificationException("Variable must not occur in a term it is unified with", root1, root2)
          }
          root2.boundTerm = root1.boundTerm
          variableStore.union(root1, root2).asInstanceOf[Var[T]]
        } else if (!root1.isBound && root2.isBound) {
          if (root2.occurs(root1)) {
            throw new UnificationException("Variable must not occur in a term it is unified with", root1, root2)
          }
          root1.boundTerm = root2.boundTerm
          variableStore.union(root1, root2).asInstanceOf[Var[T]]
        } else { // both unbound
          val res = variableStore.union(root1, root2).asInstanceOf[Var[T]]
          if (res == root1) {
            root2.boundTerm = Some(root1)
          } else {
            root1.boundTerm = Some(root2)
          }
          res
        }
      case t if root1.boundTerm == None && other.occurs(this) =>
        throw new UnificationException("Variable must not occur in a term it is unified with", this, other)
      case t if root1.boundTerm == None => // not yet bound, so bind the variable now
        root1T.boundTerm = Some(t)
        root1T
      // this variable is already bound for the following cases
      case t : Term[_] =>
        // recursively unify the terms
        root1T.boundTerm.get =:= t
        root1T
    }
  }
  
  override def occurs[VT](variable : Var[VT]) = this == variable
  
  override def substituted : Term[T] = getTerm.getOrElse(this)
  
  variableStore.register(this)(mf)
}

object Var {
  def apply[T](name : String)(implicit variableStore : VariableStore, mf : scala.reflect.Manifest[T]) = 
    variableStore.provideVar[T](name)(mf) 
}