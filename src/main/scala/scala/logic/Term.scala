package scala.logic

class UnificationException[T](reason : String, term1 : Term[T], term2 : Term[T]) extends Exception

/**
 * A general n-ary term. Terms that can be evaluated can be constructed by
 * extending both Term and Function0 and overriding the apply method accordingly.
 */
trait Term[T] { self =>
  def symbol : String
  def arity : Int
  
  def isGround : Boolean
  
  protected def _testArityAndSymbol(other : Term[T]) : self.type = 
    if (arity != other.arity) 
      throw new UnificationException("Different arities", this, other)
    else if (!symbol.equals(other.symbol))
      throw new UnificationException("Different term symbols", this, other)
    else
      this
      
  /**
   * Occur-check to test if the given variable occurs in any sub-term
   */
  def occurs[VT](variable : Var[VT]) : Boolean = false
  
  def =:= (other : Term[T]) : Term[T]
}

// The following code duplication is ugly, but apparently hard to avoid,
// as it is analogous to the Function0-22 traits, where type arguments may
// differ

trait Term0[T] extends Term[T] {
  val arity = 0
  
  override def toString = symbol
}

trait Term1[T, T1] extends Term[T] {
  val arity = 1
  
  def arg1 : Term[T1]
  
  def isGround = arg1.isGround
  
  override def toString = symbol + "(" + arg1 + ")"
  
  def =:= (other : Term[T]) = other match {
    case v : Var[_] =>
      v =:= this // turn around, as variable knows how to bind itself
      this
    case _ =>
      _testArityAndSymbol(other) 
      arg1 =:= other.asInstanceOf[Term1[_,_]].arg1.asInstanceOf[Term[T1]]
      this
  }
  
  override def occurs[VT](variable : Var[VT]) = arg1 occurs variable
}

trait Term2[T, T1, T2] extends Term[T] {
  val arity = 2
  
  def arg1 : Term[T1]
  def arg2 : Term[T2]
  
  def isGround = arg1.isGround && arg2.isGround
  
  override def toString = symbol + "(" + arg1 + "," + arg2 + ")"
  
  def =:= (other: Term[T]) = other match {
    case v : Var[_] =>
      v =:= this // turn around, as variable knows how to bind itself
      this
    case _ =>
      _testArityAndSymbol(other)
      arg1 =:= other.asInstanceOf[Term2[_,_,_]].arg1.asInstanceOf[Term[T1]]
      arg2 =:= other.asInstanceOf[Term2[_,_,_]].arg2.asInstanceOf[Term[T2]]
      this
  } 
  
  override def occurs[VT](variable : Var[VT]) = 
    (arg1 occurs variable) || (arg2 occurs variable)
}

trait Term3[T, T1, T2, T3] extends Term[T] {
  val arity = 3
  
  def arg1 : Term[T1]
  def arg2 : Term[T2]
  def arg3 : Term[T3]
  
  def isGround = arg1.isGround && arg2.isGround && arg3.isGround
  
  override def toString = symbol + "(" + arg1 + "," + arg2 + "," + arg3 + ")"
  
  def =:= (other: Term[T]) = other match {
    case v : Var[_] =>
      v =:= this // turn around, as variable knows how to bind itself
      this
    case _ =>
      _testArityAndSymbol(other)
      arg1 =:= other.asInstanceOf[Term3[_,_,_,_]].arg1.asInstanceOf[Term[T1]]
      arg2 =:= other.asInstanceOf[Term3[_,_,_,_]].arg2.asInstanceOf[Term[T2]]
      arg3 =:= other.asInstanceOf[Term3[_,_,_,_]].arg3.asInstanceOf[Term[T3]]
      this
  }
  
  override def occurs[VT](variable : Var[VT]) = 
    (arg1 occurs variable) || (arg2 occurs variable) || (arg3 occurs variable)
}