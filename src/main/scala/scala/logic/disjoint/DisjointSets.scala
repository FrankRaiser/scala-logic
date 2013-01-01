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
class DisjointSets[T](val nodes: Map[T, Node[T]] = Map.empty) {
  
  private var compressedPaths : scala.collection.mutable.Map[T, Node[T]] = 
    scala.collection.mutable.Map.empty
  
  def add(element : T) : DisjointSets[T] =
    new DisjointSets(nodes + (element -> Node(element, 0, None)) )

  @tailrec
  private def updateNodes(nodes : Map[T, Node[T]], updates: List[(Node[T], Node[T])]) : Map[T, Node[T]] =
    updates match {
    case (nodeOld, nodeNew) :: xs =>
      updateNodes( nodes.map(p =>
        if (p._2.parent == Some(nodeOld)) 
          (p._1 -> p._2.copy(parent = Some(nodeNew)))
        else 
          p) 
        + (nodeNew.elem -> nodeNew), xs)
    case _ => nodes
  }
  
  def union(elem1 : T, elem2 : T) : DisjointSets[T] = synchronized {
    require(nodes.contains(elem1) && nodes.contains(elem2), 
        "Only elements contained in the disjoint-sets can be unioned")
    require(elem1 != elem2, "Only different elements can be unioned")
        
    // retrieve representative nodes
    (nodes.get(elem1).map(_.getRepresentativeOfSet), 
     nodes.get(elem2).map(_.getRepresentativeOfSet)) match {
      
      // Case #1: both elements already in same set
      case (Some(n1), Some(n2)) if n1 == n2 => 
        new DisjointSets(nodes) // new copy due to mutable path compression

      // Case #2: rank1 > rank2 -> make n1 parent of n2
      case (Some(n1 @ Node(_, rank1, _)), 
            Some(n2 @ Node(_, rank2, _))) if rank1 > rank2 =>
        new DisjointSets(
            updateNodes(nodes, List(
                (n2 -> n2.copy(parent = Some(n1))))))
        
      // Case #3: rank1 < rank2 -> make n2 parent of n1
      case (Some(n1 @ Node(_, rank1, _)), 
            Some(n2 @ Node(_, rank2, _))) if rank1 < rank2 =>
        new DisjointSets(
            updateNodes(nodes, List(
                (n1 -> n1.copy(parent = Some(n2))))))
        
      // Case #4: rank1 == rank2 -> keep n1 as representative and increment rank
      case (Some(n1 @ Node(_, rank1, _)), 
            Some(n2 @ Node(_, rank2, _))) /*if rank1 == rank2*/ =>
        val newn1 = n1.copy(rank = rank1 + 1)
        new DisjointSets(
            updateNodes(nodes, List(
                (n1 -> newn1),
                (n2 -> n2.copy(parent = Some(newn1))))))
      
      // we are guaranteed to find the two nodes in the map,
      // and the above cases cover all possibilities
      case _ => throw new MatchError("This should never have happened")
    }
  }

  def find(elem : T) : Option[T] = synchronized {
    val startNode = compressedPaths.get(elem) match {
      case Some(node) => Some(node)
      case _ => nodes.get(elem)
    }
    
    startNode match {
      case Some(node) =>
        val rootNode = node.getRepresentativeOfSet
        compressedPaths += (elem -> rootNode)
        Some(rootNode.elem)
      case None => None
    } 
  }
  
  lazy val numberOfDisjointSets = nodes.values.count(_.parent == None)
  
  lazy val size = numberOfDisjointSets
}

object DisjointSets {
  def apply[T](initialElements : Seq[T] = Nil) = new DisjointSets[T](
      initialElements.map(e => e -> Node(e, 0, None)).toMap)
}