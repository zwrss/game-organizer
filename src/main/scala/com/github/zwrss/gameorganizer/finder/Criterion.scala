package com.github.zwrss.gameorganizer.finder

import org.joda.time.DateTime

sealed trait Criterion[E] {
  def forceOk(e: Any): Boolean = ok(e.asInstanceOf[E])

  def ok(e: E): Boolean

  def &&(that: Criterion[E]): And[E] = And(this, that)

  def ||(that: Criterion[E]): Or[E] = Or(this, that)

  def unary_! : Not[E] = Not(this)

}

case class And[E](q1: Criterion[E], q2: Criterion[E]) extends Criterion[E] {
  override def ok(e: E): Boolean = (q1 ok e) && (q2 ok e)
}

case class Or[E](q1: Criterion[E], q2: Criterion[E]) extends Criterion[E] {
  override def ok(e: E): Boolean = (q1 ok e) || (q2 ok e)
}

case class Not[E](q: Criterion[E]) extends Criterion[E] {
  override def ok(e: E): Boolean = !(q ok e)
}

case class Exists[E](field: Field[E, _]) extends Criterion[E] {
  override def ok(e: E): Boolean = (field get e).nonEmpty
}

case class Equals[E, V](field: Field[E, V], value: V) extends Criterion[E] {
  override def ok(e: E): Boolean = (field get e) contains value
}

case class Like[E, V](field: Field[E, V], value: V) extends Criterion[E] {
  override def ok(e: E): Boolean = (field get e).exists {
    case x: String => x.toLowerCase matches value.toString
    case x => x == value
  }
}

case class In[E, V](field: Field[E, V], values: Seq[V]) extends Criterion[E] {
  override def ok(e: E): Boolean = (field get e) exists values.contains
}

case class Range[E, V](field: Field[E, V], min: Option[V] = None, max: Option[V] = None) extends Criterion[E] {

  private def compare(_x: Any, cast: Any => BigDecimal): Boolean = {
    val x = cast(_x)
    min.forall(min => x >= cast(min)) && max.forall(max => x <= cast(max))
  }

  override def ok(e: E): Boolean = field.get(e).exists {
    case x: Int => compare(x, BigDecimal apply _.asInstanceOf[Int])
    case x: Long => compare(x, BigDecimal apply _.asInstanceOf[Long])
    case x: Float => compare(x, BigDecimal apply _.asInstanceOf[Float].toDouble)
    case x: Double => compare(x, BigDecimal apply _.asInstanceOf[Double])
    case x: BigDecimal => compare(x, _.asInstanceOf[BigDecimal])
    case x: String =>
      min.forall(_.toString <= x) && max.forall(_.toString >= x)
    case x: DateTime =>
      min.forall(_.asInstanceOf[DateTime] isBefore x) && max.forall(_.asInstanceOf[DateTime] isAfter x)
    case _ => false
  }
}