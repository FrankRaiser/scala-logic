package scala.logic

import scala.util.parsing.combinator.syntactical.StandardTokenParsers

object TermParser extends StandardTokenParsers {
  lexical.delimiters ++= List("(", ")", ",")
  
  def term : Parser[Term[Int]] = (numericLit ^^ {s => new Constant(2) } | ident ^^ { s => new Constant(3) } |
    ident ~ "(" ~> ident ~ repsep(ident, ",") <~ ")" ^^ { s => new Constant(1) } )
  
  def parse(s : String) : Term[Int] = {
    val tokens = new lexical.Scanner(s)
    phrase(term)(tokens).get
  }
}