package scala

package object logic {
  def ??? : Nothing = throw new Error("not implemented yet")
  
  implicit val variableStore = new VariableStore()
  
  implicit def int2Constant(i : Int) = new Constant(i)
  
  implicit def string2TermBuilder(s : String) = new TermBuilder(s)
}