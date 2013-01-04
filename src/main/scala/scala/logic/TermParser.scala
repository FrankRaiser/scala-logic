package scala.logic

import scala.util.parsing.combinator.syntactical.StandardTokenParsers
import scala.util.parsing.combinator.RegexParsers
import scala.util.parsing.combinator.JavaTokenParsers
import java.text.ParseException

object TermParser extends JavaTokenParsers {
  def term(implicit variableStore : VariableStore) : Parser[Term[Any]] = (
      floatingPointNumber ^^ { s => 
        try { new Constant[Any](s.toInt) } catch { case _ => 
          try { new Constant[Any](s.toLong) } catch { case _ => 
            new Constant[Any](s.toDouble) }
        }
      }
    | ident ~ """\(""".r ~ term <~ """\)""".r ^^ {s =>
        new Term1[Any, Any]() {
          val symbol = s._1._1
          val arg1 = s._2
        }
      }
    | ident ~ """\(""".r ~ term ~ """,""".r ~ term <~ """\)""".r ^^ {s =>
        new Term2[Any, Any, Any]() {
          val symbol = s._1._1._1._1
          val arg1 = s._1._1._2
          val arg2 = s._2
        }
      }
    | ident ~ """\(""".r ~ term ~ """,""".r ~ term ~ """,""".r ~ term <~ """\)""".r ^^ {s =>
        new Term3[Any, Any, Any, Any]() {
          val symbol = s._1._1._1._1._1._1
          val arg1 = s._1._1._1._1._2
          val arg2 = s._1._1._2
          val arg3 = s._2
        }
      }
    | """[A-Z]""".r ~ opt(ident) ^^ { s =>
      variableStore.provideVar[Any]("" + s._1 + s._2.getOrElse(""))
      }
    | ident ^^ { s => new Constant[Any](s)} 
  )
  
  def parse(s : String)(implicit variableStore : VariableStore) : Term[Any] =
    /* Parsing is synchronized due to a bug in the Scala parser combinators
     * making them non-thread-safe, which would lead to occasional NPEs otherwise
     */
    synchronized {
      this.parseAll(term(variableStore), s).get
    }
}

class TermBuilder(s : String) {
  def asTerm(implicit variableStore : VariableStore) : Term[Any] = 
    TermParser.parse(s)(variableStore)
}