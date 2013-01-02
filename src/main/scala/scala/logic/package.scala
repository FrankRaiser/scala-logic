package scala

package object logic {
  implicit val variableStore = new VariableStore()
  
  implicit def int2Constant(i : Int) = new Constant(i)
  
  implicit def string2TermBuilder(s : String) = new TermBuilder(s)
}