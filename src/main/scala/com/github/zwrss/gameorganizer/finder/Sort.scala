package com.github.zwrss.gameorganizer.finder

trait Sort[E] {

  def ordering: Ordering[E]

  def and(that: Sort[E]): Sort[E] = ComplexSort(this, that)

}

case class SimpleSort[E, V: Ordering](field: Field[E, V], ascending: Boolean = true) extends Sort[E] {

  def ordering: Ordering[E] = {
    val valueOrderingUnoriented = implicitly[Ordering[V]]
    val valueOrdering = if (ascending) valueOrderingUnoriented else valueOrderingUnoriented.reverse

    def getBestValue(e: E): Option[V] = (field get e).sorted(valueOrdering).headOption

    (x: E, y: E) =>
      (getBestValue(x), getBestValue(y)) match {
        case (Some(a), Some(b)) => valueOrdering.compare(a, b)
        case (None, Some(b)) => 1
        case (Some(a), None) => -1
        case _ => 0
      }
  }

}

case class ComplexSort[E](s1: Sort[E], s2: Sort[E]) extends Sort[E] {
  override def ordering: Ordering[E] = (x: E, y: E) => s1.ordering.compare(x, y) match {
    case 0 => s2.ordering.compare(x, y)
    case x => x
  }
}