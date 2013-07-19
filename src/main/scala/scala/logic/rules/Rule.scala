package scala.logic.rules

import scala.logic.Term

/**
 * A rule constitutes three major parts: head, guard and body
 * @author Frank Raiser
 */
trait Rule {
  def head : Seq[Term[_]]
  def body : Seq[Term[_]]
  def guard : Seq[Term[_]]
}

object Rule {
  trait Prolog {
    type RuleType = PrologRule
  }
}