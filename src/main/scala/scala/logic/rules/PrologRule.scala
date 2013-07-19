package scala.logic.rules

import scala.logic.Term
import scala.language.existentials

class PrologRule(val headPredicate : Term[_], val body : Seq[Term[_]]) extends Rule {
  val guard = Nil
  val head = List(headPredicate)
  
  override val toString = headPredicate + " :- " + body
}