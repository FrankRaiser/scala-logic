scala-logic
===========

scala-logic is a library for logic variables and quantifier-free formulae support in Scala. 

The library supports typed logic variables, general terms, matching, and unification. It does not
add a full-fledged Prolog-like search engine or constraint solving engine, but rather acts
as a core library if one wanted to implement these. (But part of this may be added in the future.)

Tests and Coverage [![Build Status](https://travis-ci.org/FrankRaiser/scala-logic.png)](https://travis-ci.org/FrankRaiser/scala-logic)
----

The library is fully tested via [specs2](http://etorreborre.github.com/specs2/) unit tests, which are 
automatically checked on different Scala and JDK versions on [Travis](http://travis-ci.org).

Test coverage is computed with the [Scala Code Coverage Tool](http://mtkopone.github.com/scct/) and
is kept at 100%. 

Usage
=====

Logic Variables
----

### Creation ###

Logic variables can be created simply as follows:

    Var[Int]("X") // create a Int-variable called X

Variables are stored in an implicit variable store, which keeps track of all performed unifications.
Hence, another call of the above code would result in the same object being returned, as there can
only be one variable `X` in a store.

### Unification ###

A logic variable can be bound to a term as follows:   

    Var("X") =:= 3 // bind the previously created variable to the term 3
    Var("X") =:= 4 // throws a UnificationException because X was bound to 3 earlier
    3 =:= Var("X") // equivalent to above, except now X is already bound to 3, so nothing is done
   
    Var("X") =:= Var("Y") // unifies the two variables
    Var("Y").value == Some(3) // true
    Var[Int]("X") =:= Var[String]("Y") // compile error
   
### Matching ###

Matching performs like a one-sided unification as follows:

    Var("X") := 3 // binds X to 3
    3 := 3 // Matches (and returns the Constant(3) )
    3 := 4 // throws a MatchException
    val x = Var[Int]("X") ; x =:= 3 ; 2 := x // throws a MatchException
    
Note that whenever a variable on the left-side of := is matched, it is actually
unified with the corresponding term on the right-side. Hence, in the following example
both variables will be bound to the constant 2:

    val x = Var[Int]("X")
    val y = Var[Int]("Y")
    x := y  // Match
    y =:= 2 // Unification
    x.getTerm == Some(Constant[Int](2)) 

Hence also the requirement to use fresh variables for the left side of the matching
performed by rule systems like Prolog or Datalog.

Terms
----

### Creation ###

A term can be created via the TermN (analogous to FunctionN) traits as follows:

    new Term1[Int, Int] {
      val symbol = "f"
      val arg1 = Constant(3)
    } // constructs the term "f(3)"
    
Terms can also be evaluatable to their type as follows:

    new Term1[Int, Int] extends Function0[Int] {
      val symbol = "f"
      val arg1 = Constant(3)
      override val apply = 3
    }.value == Some(3)
    
Variables and constants are also Term0 instances.

Variable store
----

All variables are assigned to an implicit variable store at their creation. This store keeps track
of the unifications and provides faster lookup of values, as explained by the following example:

    Var("X") =:= Var("Y") =:= Var("Z")
    Var("Z") =:= 1
    Var("X").value == Some(1)
    Var("X").value == Var("Y").value
   
Looking up the value of `X` for the first time requires to look up the values of `Y` and `Z`, before
reaching the actual value 1. The two subsequent requests, however, are optimized by the variable
store to find the value 1 in constant time (implementation is based on disjoint sets with union-by-rank
and path compression optimizations). 

### Type Checking ###

The variable store is responsible for type-checking the variables. Unfortunately, it is not possible
to include the string names of variables into the type-system, thus runtime type-checks are performed
to avoid the following:

    Var[Int]("X") =:= 3 // create X as Int
    ...
    Var[String]("X") // retrieve/re-create X as String -> throws a RuntimeException
    
Additionally, the type inferencer often allows omitting explicit typing of variables and otherwise
detects errors like the following:

    Var[String]("X") =:= 3 // compile-time error
    
Simplified Term Construction
----

The variable store can be used together with the Term object to simplify construction of terms:

    TermParser.parse("f(g(X, a), h(Y, Z, 3))")
    
After importing the main package, an implicit conversion is available, which makes the construction
even simpler:

    import scala.logic._
    ...
    "f(g(X, a), h(Y, Z, 3))".asTerm
    
The above example requires an implicit variable store for looking up X,Y, and Z. It will
create the corresponding Term2 and Term3 objects as well as Constant objects for 'a' and 3.

### Typing Restriction ###

Simplifying the term construction via strings undermines the type system, hence, the
constructed terms will all be instances of Term[Any]. As terms are invariant (due to unification)
this effectively eliminates static type-checking on these terms.

    "f(3)".asTerm =:= Var[Int]("X") // compile error
    "f(3)".asTerm =:= Var[Any]("X") // compiles, but X is effectively untyped
    "f(3)".asTerm =:= Var("X") // same as Var[Any]    

### Creating fresh terms ###

In most logic systems there is a need to create a fresh copy of terms, i.e. a copy
that uses new variables. In scala-logic all terms provide a fresh() method for this:

    scala> "f(X, g(Y, 3))".asTerm.fresh
    res0: scala.logic.Term[Any] = f(X3703095,g(Y4176104,3))

License
=======

scala-logic is released under the Apache 2.0 license (see LICENSE file for more information)

   Copyright 2012 Frank Raiser

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
