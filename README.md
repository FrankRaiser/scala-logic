scala-logic
===========

scala-logic is a library for logic variables and quantifier-free formulae support in Scala. 

The library supports typed logic variables, general terms, and unification. It does not
add a full-fledged Prolog-like search engine or constraint solving engine, but rather acts
as a core library if one wanted to implement these.

It further provides a simple constraint-like support for standard operators.

Usage
=====

Creating logic variables
----

Logic variables can be created simply as follows:

    Var[Int]("X") // create a Int-variable called X

Variables are stored in an implicit variable store, which keeps track of all performed unifications.
Hence, another call of the above code would result in the same object being returned, as there can
only be one variable `X` in a store.

Unification
----

A logic variable can be bound to a term as follows:   

    Var("X") =:= 3 // bind the previously created variable to the term 3
    Var("X") =:= 4 // throws a UnificationError because X was bound to 3 earlier
    3 =:= Var("X") // equivalent to above, except now X is already bound to 3, so nothing is done
   
    Var("X") =:= Var("Y") // unifies the two variables
    Var("Y").value == Some(3) // true
    Var[Int]("X") =:= Var[String]("Y") // compile error
   
Terms
----

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
====

All variables are assigned to an implicit variable store at their creation. This store keeps track
of the unifications and provides faster lookup of values, as explained by the following example:

    Var("X") =:= Var("Y") =:= Var("Z")
    Var("Z") =:= 1
    Var("X").value == Some(1)
    Var("X").value == Var("Y").value
   
Looking up the value of `X` for the first time requires to look up the values of `Y` and `Z`, before
reaching the actual value 1. The two subsequent requests, however, are optimized by the variable
store to find the value 1 in constant time. 

Type Checking
----

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

TODO - not implemented yet
The variable store can be used together with the Term object to simplify construction of terms:

    Term.parse("f(g(X, a), h(Y, Z, 3))")
    
The above example requires an implicit variable store for looking up X,Y, and Z. It will
create the corresponding Term2 and Term3 objects as well as Constant objects for 'a' and 3.
    
Term Store
====

In contrast to the variable store, the term store stores several terms, which logically correspond to
a conjunction. Term simplification is performed when adding new terms to the store, as seen in the
following examples:

    TODO term store and examples not yet implemented
    
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
