package scala.logic.state

import scala.logic.TermStore
import scala.logic.Term

/**
 * A simple state, which holds a list of terms.
 *  
 * @author Frank Raiser
 */
case class TermState(val terms : List[Term[_]]) extends State {
}