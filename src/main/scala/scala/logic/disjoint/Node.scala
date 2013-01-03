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