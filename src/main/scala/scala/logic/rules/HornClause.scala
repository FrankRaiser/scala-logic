package scala.logic.rules

import scala.logic.Term
import scala.language.existentials

class HornClause(val headPredicate : Term, val body : Seq[Term] = Nil) extends Rule {
  val guard = Nil
  val head = List(headPredicate)
  
  override val toString = headPredicate + " :- " + body
}