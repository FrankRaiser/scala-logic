package scala.logic.unification

import org.junit.runner.RunWith
import scala.logic.state.TermState

class UnificationTermStateSpec extends UnificationSpec[TermState] {
  
  val emptyState = new TermState(Nil)
}