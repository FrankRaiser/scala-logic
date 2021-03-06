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
 * This class implements a disjoint-sets algorithm with
 * union-by-rank and path compression. The forest/sets/etc. are
 * internal data structures not exposed to the outside. Instead,
 * it is possible to add new elements (which end up in a newly
 * created set), union two elements, and hence, their sets, and
 * find the representative of a disjoint-set by a given element of
 * the set.
 *
 * Objects of this class are almost immutable. All operations
 * return a new updated object. To implement path compression, however,
 * a map with the compressed paths is kept separately.
 *
 * @author Frank Raiser
 */
class DisjointSets[T](val nodes: Map[T, Node[T]] = Map.empty) extends Equals {

  private var compressedPaths : scala.collection.mutable.Map[T, T] =
    scala.collection.mutable.Map.empty

  // scalastyle:off
  // I deem + symbols appropriate operators for adding to a set structure
  def + (element : T) : DisjointSets[T] = add(element)
  def ++(elements : Seq[T]) : DisjointSets[T] = addAll(elements)
  // scalastyle:on

  def add(element : T) : DisjointSets[T] =
    new DisjointSets(nodes + (element -> Node(element, 0, None)) )

  def addAll(elements : Seq[T]) : DisjointSets[T] =
    new DisjointSets(nodes ++ elements.map(e => e -> Node(e, 0, None)) )

  private def createDisjointSetsWithUpdatedNodesAndCompressedPaths(
      updates : List[(Node[T], Node[T])]) : DisjointSets[T] = {
    val updatedNodes = nodes ++ (updates.map(p => p._2.elem -> p._2))
    val ds = new DisjointSets(updatedNodes)
    ds.compressedPaths ++= this.compressedPaths
    ds
  }

  @tailrec
  final def getRepresentativeOfSet(elem : T): Node[T] = {
    val node = nodes.get(elem)
    require(node.isDefined, "element must be in disjoint sets")
    node.get.parent match {
      case None => node.get
      case Some(parentElem) =>
        getRepresentativeOfSet(parentElem)
    }
  }

  def union(elem1 : T, elem2 : T) : DisjointSets[T] = synchronized {
    require(nodes.contains(elem1) && nodes.contains(elem2),
        "Only elements contained in the disjoint-sets can be unioned")
    require(elem1 != elem2, "Only different elements can be unioned")

    val p = (getRepresentativeOfSet(elem1), getRepresentativeOfSet(elem2))
    // we are guaranteed to find the two nodes in the map,
    // and the below cases cover all possibilities
    (p : @unchecked) match {

      // Case #1: both elements already in same set
      case (n1, n2) if n1 == n2 =>
        createDisjointSetsWithUpdatedNodesAndCompressedPaths(List())

      // Case #2: rank1 > rank2 -> make n1 parent of n2
      case (n1 @ Node(_, rank1, _),
            n2 @ Node(_, rank2, _)) if rank1 > rank2 =>
        createDisjointSetsWithUpdatedNodesAndCompressedPaths(List(
            (n2 -> n2.copy(parent = Some(n1.elem)))))

      // Case #3: rank1 < rank2 -> make n2 parent of n1
      case (n1 @ Node(_, rank1, _),
            n2 @ Node(_, rank2, _)) if rank1 < rank2 =>
        createDisjointSetsWithUpdatedNodesAndCompressedPaths(List(
            (n1 -> n1.copy(parent = Some(n2.elem)))))

      // Case #4: rank1 == rank2 -> keep n1 as representative and increment rank
      case (n1 @ Node(_, rank1, _),
            n2 @ Node(_, rank2, _)) /*if rank1 == rank2*/ =>
        val newn1 = n1.copy(rank = rank1 + 1)
        createDisjointSetsWithUpdatedNodesAndCompressedPaths(List(
            (n1 -> newn1),
            (n2 -> n2.copy(parent = Some(newn1.elem)))))
    }
  }

  def find(elem : T) : Option[T] = synchronized {
    val startNode = compressedPaths.get(elem).map(nodes.get(_)) match {
      case Some(node) => node
      case _ => nodes.get(elem)
    }

    startNode match {
      case Some(node) =>
        val rootNode = getRepresentativeOfSet(node.elem)
        compressedPaths += (elem -> rootNode.elem)
        Some(rootNode.elem)
      case None => None
    }
  }

  lazy val numberOfDisjointSets = nodes.values.count(_.parent == None)

  lazy val size = numberOfDisjointSets

  def canEqual(other: Any) : Boolean = {
    other.isInstanceOf[scala.logic.disjoint.DisjointSets[T]]
  }

  override def equals(other: Any) : Boolean = {
    other match {
      case that: scala.logic.disjoint.DisjointSets[T] => that.canEqual(DisjointSets.this) && nodes == that.nodes
      case _ => false // covered, but not detected by scct due to a bug :(
    }
  }

  override def hashCode() : Int = {
    val prime = 41
    prime + nodes.hashCode
  }
}

object DisjointSets {
  def apply[T](initialElements : Seq[T] = Nil) : DisjointSets[T] = new DisjointSets[T](
      initialElements.map(e => e -> Node(e, 0, None)).toMap)
}
