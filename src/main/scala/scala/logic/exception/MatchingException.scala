package scala.logic.exception

import scala.logic.Term

class MatchingException[T](reason : String, term1 : Term[T], term2 : Term[T]) 
	extends UnificationException(reason, term1, term2)