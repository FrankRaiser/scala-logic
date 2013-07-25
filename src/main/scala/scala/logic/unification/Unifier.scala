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
package scala.logic.unification

import scala.logic.state.State
import scala.logic.disjoint.DisjointSets
import scala.logic._
import scala.logic.exception.UnificationException
import scalaz._
import scala.logic.exception.UnificationException
import scala.logic.exception.UnificationException
import scala.logic.exception.UnificationException
import scala.logic.exception.UnificationException

/**
 * The Unifier object implements unification of arbitrary terms, based
 * on the given terms and their context in the form of existing variable
 * bindings and a disjointset structure.
 *
 * @author Frank Raiser
 */
object Unifier {

  import Scalaz._

  type UnificationContext = State
  type UnificationResult = Validation[UnificationException, UnificationContext]

  /**
   * Performs the actual unification within the given context and returns
   * a new context that results from the unification, or an exception explaining
   * why the unification was not possible.
   */
  def unify(term1 : Term, term2 : Term, context : UnificationContext) : UnificationResult = term1 match {
    case v : Var => unifyVar(v, term2, context)
    case c : Constant => unifyConstant(c, term2, context)
    case _ => unifyTerm(term1, term2, context)
  }

  def matchTerms(term1 : Term, term2 : Term, context : UnificationContext) : UnificationResult = term1 match {
    case v : Var => unifyVar(v, term2, context)
    case c : Constant => matchConstant(c, term2, context)
    case _ => matchTerm(term1, term2, context)
  }

  private def unifyVar(var1 : Var, term2 : Term, context : UnificationContext,
      recursiveCall : (Term, Term, UnificationContext) => UnificationResult = unify) : UnificationResult =
    context.disjointSets.find(var1) match {
      case None => unifyVar(var1, term2, context.addVariables(List(var1)))
      case Some(rootVar) => unifyRootVar(rootVar, term2, context, recursiveCall)
    }

  private def unifyRootVar(rootVar : Var, term2 : Term, context : UnificationContext,
      recursiveCall : (Term, Term, UnificationContext) => UnificationResult = unify) : UnificationResult =
    term2 match {
        case c : Constant => unifyVarConstant(rootVar, c, context)
        case v2 : Var if context.disjointSets.find(v2) == Some(rootVar) => context.success
        case t : Term if t.occurs(rootVar) =>
          new UnificationException("Variable must not occur in bound term", rootVar, term2).fail
        case _ => context.boundTerm(rootVar) match {
          case Some(boundTerm) => recursiveCall(boundTerm, term2, context)
          case None => context.bind(rootVar, term2).success
        }
      }

  private def unifyConstant(c : Constant, term2 : Term, context : UnificationContext) : UnificationResult = term2 match {
    case d : Constant if c == d => context.success
    case d : Constant => new UnificationException("Constants differ", c, d).fail
    case v : Var => v.getTerm(context) match {
      case None => context.disjointSets.find(v) match {
        case Some(rootVar) => unifyVarConstant(rootVar, c, context)
        case None => unifyVarConstant(v, c, context.addVariables(List(v)))
      }
      case Some(boundTerm) => unifyConstant(c, boundTerm, context)
    }
    case _ => new UnificationException("Cannot unify constant with second term", c, term2).fail
  }

  private def matchConstant(c : Constant, term2 : Term, context : UnificationContext) : UnificationResult = term2 match {
    case v : Var => v.getTerm(context) match {
      case None => new UnificationException("Cannot match unbound variable to a constant", c, v).fail
      case Some(boundTerm) => matchConstant(c, boundTerm, context)
    }
    case _ => unifyConstant(c, term2, context)
  }

  private def unifyTerm(term1 : Term, term2 : Term, context : UnificationContext,
      recursiveCall : (Term, Term, UnificationContext) => UnificationResult = unify) : UnificationResult = term2 match {
    case c : Constant => new UnificationException("non-constant Term and constant cannot be unified", term1, term2).fail
    case v : Var => unifyVar(v, term1, context)
    case t : Term if term1.arity != t.arity =>
      new UnificationException("Terms with different arities cannot be unified", term1, term2).fail
    case t : Term if term1.symbol != t.symbol=>
      new UnificationException("Terms with different symbols cannot be unified", term1, term2).fail
    case _ =>
      val argsZipped : List[(Term, Term)] = (term1.arguments zip term2.arguments)
      def foldedUnify(res : UnificationResult, terms : (Term, Term)) : UnificationResult =
        for {
          c <- res
          res <- recursiveCall(terms._1, terms._2, c)
        } yield res

      val initial : UnificationResult = context.success
      argsZipped.foldLeft(initial)(foldedUnify)
  }

  private def matchTerm(term1 : Term, term2 : Term, context : UnificationContext) : UnificationResult = term2 match {
    case v : Var => v.getTerm(context) match {
      case None => new UnificationException("A term cannot be matched to an unbound variable", term1, v).fail
      case Some(boundTerm) => matchTerms(term1, boundTerm, context)
    }
    case _ => unifyTerm(term1, term2, context, matchTerms)
  }

  private def unifyVarConstant(rootVar : Var, c : Constant, context : UnificationContext) : UnificationResult =
      context.boundTerm(rootVar) match {
    case None => context.bind(rootVar, c).success
    case Some(t) if t == c => context.success
    case _ => new UnificationException("Term bound to var is not same constant", rootVar, c).fail
  }
}
