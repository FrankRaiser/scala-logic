package scala.logic

class UnificationException[T](val reason : String, val term1 : Term[T], val term2 : Term[T]) extends Exception(reason)

/**
 * A general n-ary term. Terms that can be evaluated can be constructed by
 * extending both Term and Function0 and overriding the apply method accordingly.
 * @author Frank Raiser
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
  
  /** unification with other term */
  def =:= (other : Term[T]) : Term[T]
  
  /** returns the current term, after all variable substitutions
   * have been applied to it.
   */
  def substituted : Term[T]
}

// The following code duplication is ugly, but apparently hard to avoid,
// as it is analogous to the Function0-22 traits, where type arguments may
// differ

trait Term0[T] extends Term[T] {
  val arity = 0
  
  override def toString = symbol 
  
  def substituted : Term[T] = this
}

trait Term1[T, T1] extends Term[T] { self =>
  val arity = 1
  
  def arg1 : Term[T1]
  
  def isGround = arg1.isGround
  
  override def toString = symbol + "(" + arg1 + ")"
  
  override def equals(other : Any) = other match {
    case t : Term1[_,_] => symbol == t.symbol && arg1 == t.arg1
    case _ => false
  }
  
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
  
  def substituted : Term[T] = new Term1[T, T1]() {
    val symbol = self.symbol
    val arg1 = self.arg1.substituted
  }
}

trait Term2[T, T1, T2] extends Term[T] { self =>
  val arity = 2
  
  def arg1 : Term[T1]
  def arg2 : Term[T2]
  
  def isGround = arg1.isGround && arg2.isGround
  
  override def toString = symbol + "(" + arg1 + "," + arg2 + ")"
  
  override def equals(other : Any) = other match {
    case t : Term2[_,_,_] => symbol == t.symbol && arg1 == t.arg1 && arg2 == t.arg2
    case _ => false
  }
  
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
    
  def substituted : Term[T] = new Term2[T, T1, T2]() {
    val symbol = self.symbol
    val arg1 = self.arg1.substituted
    val arg2 = self.arg2.substituted
  }
}

trait Term3[T, T1, T2, T3] extends Term[T] { self =>
  val arity = 3
  
  def arg1 : Term[T1]
  def arg2 : Term[T2]
  def arg3 : Term[T3]
  
  def isGround = arg1.isGround && arg2.isGround && arg3.isGround
  
  override def toString = symbol + "(" + arg1 + "," + arg2 + "," + arg3 + ")"
  
  override def equals(other : Any) = other match {
    case t : Term3[_,_,_,_] => symbol == t.symbol && 
    		arg1 == t.arg1 && arg2 == t.arg2 && arg3 == t.arg3
    case _ => false
  }
  
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
    
  def substituted = new Term3[T, T1, T2, T3]() {
    val symbol = self.symbol
    val arg1 = self.arg1.substituted
    val arg2 = self.arg2.substituted
    val arg3 = self.arg3.substituted
  }
}