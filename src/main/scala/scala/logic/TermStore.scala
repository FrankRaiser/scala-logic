package scala.logic

/**
 * A term store keeps track of a multi-set of terms. Each store needs a corresponding
 * variable store that needs to be shared by the terms, i.e. the terms may not use
 * variables from different variable stores.
 * @author Frank Raiser
 */
class TermStore[T](val terms : List[Term[T]] = Nil) {
  
  def isEmpty = terms.isEmpty
  
  def +(term : Term[T]) : TermStore[T] = new TermStore(term :: terms)
  
  def -(term : Term[T]) : TermStore[T] = new TermStore(terms.filterNot(_ == term))
  
  def --(otherTerms : List[Term[T]]) : TermStore[T] = new TermStore(terms.filterNot(otherTerms.contains))
  
  def ++(otherTerms : List[Term[T]]) : TermStore[T] = new TermStore(otherTerms ++ terms)
}