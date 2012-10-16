package scala.logic.disjoint

import scala.annotation.tailrec

/**
 * This class implements a disjoint sets algorithm with
 * union-by-rank and path compression. The forest/sets/etc. are
 * internal data structures not exposed to the outside. Instead,
 * it is possible to add new elements (which end up in a newly
 * created set), union two elements, and hence, their sets, and
 * find the representative of a disjoint set by a given element of
 * the set.
 * 
 * @todo make the class iterable to iterate through the representatives
 */
class DisjointSets[T](initialElements : Seq[T] = Nil) {

  /**
   * Add a new element to the disjoint set forest. It will
   * be placed into its own set.
   */
  def add(elem : T) : Unit = synchronized {
    nodes += (elem -> DisjointSets.Node(elem, 0, None))
  }
  
  /**
   * Union the disjoint sets of which <code>elem1</code> and <code>elem2</code> are members
   * of.
   * @return the representative of the unioned set
   * @precondition elem1 and elem2 must have been added before
   */
  def union(elem1 : T, elem2 : T) : T = synchronized {
    // find representatives
    val rep1 = find(elem1)
    val rep2 = find(elem2)
    require(rep1 != None && rep2 != None, 
        "Only elements previously added to the disjoint set can be unioned")

    // Distinguish the different union cases and return the new set representative
    if (rep1 == rep2) rep1.get
    else (rep1.map(nodes), rep2.map(nodes)) match {
  
      // Case #1: rank1 > rank2 -> make n1 parent of n2
      case (Some(n1 @ DisjointSets.Node(_, rank1, None)), 
            Some(n2 @ DisjointSets.Node(_, rank2, None))) if rank1 > rank2 =>
        n2.parent = Some(n1)
        n1.elem
        
      // Case #2: rank1 < rank2 -> make n2 parent of n1
      case (Some(n1 @ DisjointSets.Node(_, rank1, None)), 
            Some(n2 @ DisjointSets.Node(_, rank2, None))) if rank1 < rank2 =>
        n1.parent = Some(n2)
        n2.elem
        
      // Case #3: rank1 == rank2 -> keep n1 as representative and increment rank
      case (Some(n1 @ DisjointSets.Node(_, rank1, None)), 
            Some(n2 @ DisjointSets.Node(_, rank2, None))) /*if rank1 == rank2*/ =>
        n1.rank = rank1 + 1
        n2.parent = Some(n1)
        n1.elem
      
      // we must find the two nodes in the map, as find returned a Some,
      // and the above cases cover all possibilities
      case _ => throw new RuntimeException("This should never have happened")
    }
  }
  
  /**
   * Finds the representative for a disjoint set, of which
   * <code>elem</code> is a member of.
   */
  def find(elem : T) : Option[T] = synchronized {
    nodes.get(elem) match {
      case Some(node) =>
        val rootNode = node.getRepresentative
        // path compression
        if (node != rootNode) node.parent = Some(rootNode)
        Some(rootNode.elem)
      case None => None
    } 
  }
  
  /**
   * Returns the number of disjoint sets managed in this data structure.
   * Keep in mind: we do not keep track of the number of sets, so this 
   * method recomputes them each time.
   */
  def size : Int = synchronized {
    nodes.values.count(_.parent == None)
  }
  
  ////
  // Internal parts
  private val nodes : scala.collection.mutable.Map[T, DisjointSets.Node[T]] = scala.collection.mutable.Map.empty
  
  // Initialization
  initialElements map (add _)
}

object DisjointSets {
  def apply[T](initialElements : Seq[T] = Nil) = new DisjointSets[T](initialElements)
  
  // Internal parts
  private case class Node[T](val elem : T, var rank : Int, var parent : Option[Node[T]]) {
    /**
     * Compute representative of this set.
     * @return root element of the set
     */
    @tailrec
    final def getRepresentative: Node[T] = parent match {
      case None => this
      case Some(p) => p.getRepresentative
    } 
  }
}