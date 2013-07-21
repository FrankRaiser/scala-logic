package scala.logic.state

import org.specs2.mutable.Specification
import org.junit.runner.RunWith
import org.specs2.runner.JUnitRunner
import org.specs2.specification.Scope
import scala.logic.exception.UnificationException
import scala.logic.VariableStore
import scala.logic._

@RunWith(classOf[JUnitRunner])
object TermStateUnificationSpec extends UnificationSpec[TermState] {
  
  val emptyState = TermState(Nil)
}