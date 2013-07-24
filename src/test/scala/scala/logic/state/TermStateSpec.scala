package scala.logic.state

import org.specs2.mutable.Specification
import org.junit.runner.RunWith
import org.specs2.runner.JUnitRunner
import org.specs2.specification.Scope
import scala.logic._

@RunWith(classOf[JUnitRunner])
object TermStateSpec extends StateSpec[TermState] {
 
  def emptyState = new TermState(Nil)
}