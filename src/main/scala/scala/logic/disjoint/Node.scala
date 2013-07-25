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
package scala.logic.disjoint

import scala.annotation.tailrec

/**
 * A single node in a disjoint-sets forest.
 * The parent only points to the element, not a concrete node, as we would
 * otherwise have to invest a lot of time to update all parent-relations,
 * when a node is modified.
 *
 * @author Frank Raiser
 */
case class Node[T](val elem : T, val rank : Int, val parent : Option[T])
