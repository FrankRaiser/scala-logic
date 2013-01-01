package scala.logic.disjoint

import org.specs2.mutable.Specification
import org.junit.runner.RunWith
import org.specs2.runner.JUnitRunner
import org.specs2.specification.Scope

@RunWith(classOf[JUnitRunner])
object DisjointSetsSpec extends Specification {
  
  trait data extends Scope {
    val ds = DisjointSets[Int](1 to 5)
  }
  
  "The disjoint sets data structure" should {
    "be empty initially" in {
      DisjointSets().size must be equalTo(0)
    }
    "take initial elements" in {
      DisjointSets(1 to 10).size must be equalTo(10)
    }
    "return element itself for non-unioned elements" in new data {
      for (i <- 1 to 5) ds.find(i) must beEqualTo(Some(i))
      ds.add(6).find(6) must be equalTo(Some(6))
    }
    "return None for elements not in the disjoint sets" in new data {
      ds.find(0) must beNone
      ds.find(6) must beNone
    }
    "return the same element as representative of a union" in {
      val ds = DisjointSets(1 to 2)
      val unioned = ds.union(1, 2)
      unioned.find(1) must be equalTo(unioned.find(2))
    }
    "work for a more complex example" in {
      // Example taken from Cormen, et.al., Introduction to Algorithms (p.500)
      val ds = List( // edges
          'b' -> 'd',
          'e' -> 'g',
          'a' -> 'c',
          'h' -> 'i',
          'a' -> 'b',
          'e' -> 'f',
          'b' -> 'c').foldLeft(DisjointSets[Char]('a' to 'j'))(
              (dset, p) => dset.union(p._1, p._2))
      // check result
      val r1 = ds.find('a')
      for (c <- List('a', 'b', 'c', 'd')) ds.find(c) must be equalTo(r1)
      ds.size must be equalTo(4)
    }
  }
}