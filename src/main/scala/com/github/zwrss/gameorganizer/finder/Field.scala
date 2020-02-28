package com.github.zwrss.gameorganizer.finder


abstract class Field[E, V: Ordering] {

  def name: String

  def get(e: E): Seq[V]

  def serialize(v: V): String

  def deserialize(v: String): V

  def ===(value: V): Equals[E, V] = Equals[E, V](this, value)

  def like(value: V): Like[E, V] = Like[E, V](this, value)

  def in(value: V, values: V*): In[E, V] = In[E, V](this, value :: values.toList)

  def <==(value: V): Range[E, V] = Range[E, V](this, max = Option(value))

  def >==(value: V): Range[E, V] = Range[E, V](this, min = Option(value))

  def between(min: V): BetweenExt[E, V] = BetweenExt[E, V](this, min)

  def missing: Not[E] = !present

  def present: Exists[E] = Exists[E](this)

  def asc: Sort[E] = SimpleSort[E, V](this, true)

  def desc: Sort[E] = SimpleSort[E, V](this, false)

  def _getOrdering: Ordering[V] = implicitly

}

case class BetweenExt[E, V](field: Field[E, V], min: V) {
  def and(max: V): Range[E, V] = Range(field, Option(min), Option(max))
}

abstract class SimpleField[E, V: Ordering](_name: String, _get: E => V) extends Field[E, V] {

  final override def name: String = _name

  final override def get(e: E): Seq[V] = Seq(_get(e))

}

abstract class IterableField[E, V: Ordering](_name: String, _get: E => Iterable[V]) extends Field[E, V] {

  final override def name: String = _name

  final override def get(e: E): Seq[V] = _get(e).toSeq

}

abstract class OptionalField[E, V: Ordering](_name: String, _get: E => Option[V]) extends Field[E, V] {

  final override def name: String = _name

  final override def get(e: E): Seq[V] = _get(e).toSeq

}

trait StringField[E] {
  this: Field[E, String] =>

  def serialize(v: String): String = v

  def deserialize(v: String): String = v

}

trait BooleanField[E] {
  this: Field[E, Boolean] =>

  override def serialize(v: Boolean): String = v.toString

  override def deserialize(v: String): Boolean = v.toBoolean

}

trait BigDecimalField[E] {
  this: Field[E, BigDecimal] =>

  override def serialize(v: BigDecimal): String = v.toString

  override def deserialize(v: String): BigDecimal = BigDecimal(v)

}