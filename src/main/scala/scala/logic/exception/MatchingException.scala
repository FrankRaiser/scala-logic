package scala.logic.exception

import scala.logic.Term

class MatchingException(reason : String, term1 : Term, term2 : Term) 
	extends UnificationException(reason, term1, term2)