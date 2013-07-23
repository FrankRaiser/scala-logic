package scala.logic.rules

import scala.logic.Term
import scala.language.existentials

class PrologRule(val headPredicate : Term, val body : Seq[Term]) extends Rule {
  val guard = Nil
  val head = List(headPredicate)
  
  override val toString = headPredicate + " :- " + body
}