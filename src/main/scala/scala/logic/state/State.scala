package scala.logic.state

/**
 * A state that a program execution can be in.
 * 
 * @author Frank Raiser
 */
trait State { }

object State {
  trait Terms {
    type StateType = TermState
  }
}