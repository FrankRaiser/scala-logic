package scala.logic.rules

import scala.logic.Term
import scala.language.existentials

class SimpleTermRule(val head: Seq[Term], val body : Seq[Term] = Nil, val guard : Seq[Term] = Nil) extends Rule {
  override val toString = head + " -> " + guard + " | " + body
}