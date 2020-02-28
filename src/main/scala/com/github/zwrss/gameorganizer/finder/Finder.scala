package com.github.zwrss.gameorganizer.finder

trait Finder[Id, E] {

  def get(id: Id): E

  def find(query: Option[Criterion[E]] = None, sort: Option[Sort[E]] = None, offset: Long = 0, limit: Long = 10): Seq[E]

  def count(query: Option[Criterion[E]] = None): Long

  def terms(query: Option[Criterion[E]], fields: Seq[Field[E, _]]): Map[Field[E, Any], Map[Any, Long]]

}
