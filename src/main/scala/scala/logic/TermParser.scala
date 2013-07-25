// Copyright (C) 2013 Frank Raiser
// See the LICENCE file distributed with this work for additional
// information regarding copyright ownership.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
package scala.logic

import scala.util.parsing.combinator.syntactical.StandardTokenParsers
import scala.util.parsing.combinator.RegexParsers
import scala.util.parsing.combinator.JavaTokenParsers
import java.text.ParseException
import scala.logic.state.State

object TermParser extends JavaTokenParsers {
  def term : Parser[Term] = (
      floatingPointNumber ^^ { s => new Constant(s) }
      // TODO unify term parsing into one expression
    | ident ~ """\(""".r ~ term <~ """\)""".r ^^ {s =>
        new Term() {
          val symbol = s._1._1
          val arity = 1
          val arguments = List(s._2)
        }
      }
    | ident ~ """\(""".r ~ term ~ """,""".r ~ term <~ """\)""".r ^^ {s =>
        new Term() {
          val arity = 2
          val symbol = s._1._1._1._1
          val arguments = List(s._1._1._2, s._2)
        }
      }
    | ident ~ """\(""".r ~ term ~ """,""".r ~ term ~ """,""".r ~ term <~ """\)""".r ^^ {s =>
        new Term() {
          val arity = 3
          val symbol = s._1._1._1._1._1._1
          val arguments = List(s._1._1._1._1._2, s._1._1._2, s._2)
        }
      }
    | """[A-Z]""".r ~ opt(ident) ^^ { s => new Var(s._1 + s._2.getOrElse("")) }
    | ident ^^ { s => new Constant(s) }
  )

  def parse(s : String) : Term =
    /* Parsing is synchronized due to a bug in the Scala parser combinators
     * making them non-thread-safe, which would lead to occasional NPEs otherwise
     */
    synchronized {
      this.parseAll(term, s).get
    }
}

class TermBuilder(s : String) {
  def asTerm : Term =
    TermParser.parse(s)
}
