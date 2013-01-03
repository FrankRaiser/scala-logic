package scala.logic.exception

import scala.logic.Term

class UnificationException[T](val reason : String, val term1 : Term[T], val term2 : Term[T]) extends Exception(reason)