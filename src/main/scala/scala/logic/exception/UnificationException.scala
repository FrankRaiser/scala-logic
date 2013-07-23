package scala.logic.exception

import scala.logic.Term

class UnificationException(val reason : String, val term1 : Term, val term2 : Term) extends Exception(reason) {
}