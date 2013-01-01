package scala.logic.disjoint

import scala.annotation.tailrec

/**
 * A single node in a disjoint-sets forest.
 * 
 * @author Frank Raiser
 */
case class Node[T](val elem : T, val rank : Int, val parent : Option[Node[T]]) {
    @tailrec
    final def getRepresentativeOfSet: Node[T] = parent match {
      case None => this
      case Some(p) => p.getRepresentativeOfSet
    } 
  }