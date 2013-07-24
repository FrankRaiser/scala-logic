scala-logic
===========

scala-logic is a library for logic variables and quantifier-free formulae support in Scala. 

The library currently supports logic variables, general terms, matching, and unification.
Planned for the future are various execution semantics (for example a Prolog-like search).

Tests and Coverage [![Build Status](https://travis-ci.org/FrankRaiser/scala-logic.png)](https://travis-ci.org/FrankRaiser/scala-logic)
----

The library is fully tested via [specs2](http://etorreborre.github.com/specs2/) unit tests, which are 
automatically checked on different Scala and JDK versions on [Travis](http://travis-ci.org).

Test coverage is computed with the [Scala Code Coverage Tool](http://mtkopone.github.com/scct/) and
is trying to be close to 100%. Currently, it is only below 100% due to a defect in scct not detecting 
the coverage. 

Usage
=====

Unification
----

To perform unification you need a state as context and two terms, which you want to unify.
You can use the TermParser and available implicits to quickly generate the corresponding Term structures:

    import scala.logic._
    import scala.logic.state.TermState
    import scala.logic.unification.Unifier
    
    // create the terms
    val term1 = "f(a, g(X), Y)".asTerm
    val term2 = "f(a, Y, g(a))".asTerm
    val term3 = "f(a, Y, g(b))".asTerm
    
    // create a state that knows the variables
    val state = new TermState(Nil).addVariables((term1.variables ++ term2.variables).toSeq)
    
    // perform the unification - result is a scalaz Validation
    val result = Unifier.unify(term1, term2, state)
    
    result.toOption != None // check that it worked - true
    val stateAfter = result.toOption.get // new state with bindings
    
    stateAfter.boundTerm(new Var("X")) == Some("a".asTerm) // X is bound to a
    stateAfter.boundTerm(new Var("Y")) == Some("g(X)".asTerm) // Y is bound to g(X)
    // after fully substituting variables, we find Y is bound to g(a)
    stateAfter.boundTerm(new Var("Y")).get.substituted(stateAfter) == Some("g(a)".asTerm)
    
    // other unification calls:
    Unifier.unify(term1, term3, stateAfter) // fails as X = a
    Unifier.unify(term1, term3, state) // succeeds and binds X = b in new result state

Matching
----

Matching is performed similarly to unification based on the following call:
    
    Unifier.matchTerms(term1, term2, state)
    
The only difference to unification is that matching will never bind an unbound variable occuring
in the second term, i.e. matching is a one-sided unification, which can be seen from the
following example:

    scala> Unifier.matchTerms("X".asTerm, "a".asTerm, state)
    res3: scala.logic.unification.Unifier.UnificationResult = Success(TermState(List(),Map(X -> a),scala.logic.disjoint.DisjointSets@b85fb7a8))
    
    scala> Unifier.matchTerms("a".asTerm, "X".asTerm, state)
    res4: scala.logic.unification.Unifier.UnificationResult = Failure(scala.logic.exception.UnificationException: Cannot unify constant with second term)

Simplified Term Construction
----

The variable store can be used together with the Term object to simplify construction of terms:

    TermParser.parse("f(g(X, a), h(Y, Z, 3))")
    
After importing the main package, an implicit conversion is available, which makes the construction
even simpler:

    import scala.logic._
    ...
    "f(g(X, a), h(Y, Z, 3))".asTerm
    
### Creating fresh terms ###

In most logic systems there is a need to create a fresh copy of terms, i.e. a copy
that uses new variables. In scala-logic all terms provide a fresh method for this:

    scala> "f(X, g(Y, 3))".asTerm.fresh
    res0: scala.logic.Term[Any] = f(X3703095,g(Y4176104,3))

License
=======

scala-logic is released under the Apache 2.0 license (see LICENSE file for more information)

   Copyright 2012-2013 Frank Raiser

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
