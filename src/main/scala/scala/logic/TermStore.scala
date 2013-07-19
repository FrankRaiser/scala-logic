package scala.logic

/**
 * A term store keeps track of a multi-set of terms. Each store needs a corresponding
 * variable store that needs to be shared by the terms, i.e. the terms may not use
 * variables from different variable stores.
 * @author Frank Raiser
 */
class TermStore(val terms : List[Term[_]] = Nil) {
  
  def isEmpty = terms.isEmpty
  
  def +[T](term : Term[T]) : TermStore = new TermStore(term :: terms)
  
  def -[T](term : Term[T]) : TermStore = new TermStore(terms.filterNot(_ == term))
  
  def --(otherTerms : List[Term[_]]) : TermStore = new TermStore(terms.filterNot(otherTerms.contains))
  
  def ++(otherTerms : List[Term[_]]) : TermStore = new TermStore(otherTerms ++ terms)
}