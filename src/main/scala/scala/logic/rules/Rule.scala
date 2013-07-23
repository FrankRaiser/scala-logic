package scala.logic.rules

import scala.logic.Term

/**
 * A rule constitutes three major parts: head, guard and body
 * @author Frank Raiser
 */
trait Rule {
  def head : Seq[Term]
  def body : Seq[Term]
  def guard : Seq[Term]
}

object Rule {
  trait Prolog {
    type RuleType = PrologRule
  }
}