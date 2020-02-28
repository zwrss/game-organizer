package com.github.zwrss.gameorganizer.finder

abstract class SimpleMemoryFinder[Id, E] extends Finder[Id, E] {

  def objects: Seq[E]

  def getId(e: E): Id

  final override def get(id: Id): E = objects.find(o => getId(o) == id) getOrElse sys.error(s"Cannot find object with id $id")

  final override def find(query: Option[Criterion[E]] = None, sort: Option[Sort[E]] = None, offset: Long = 0, limit: Long = 10): Seq[E] = {
    var result = objects
    query.foreach(q => result = result filter q.ok)
    sort.foreach(s => result = result sorted s.ordering)
    result = result drop offset.toInt
    result = result take limit.toInt
    result
  }

  override def count(query: Option[Criterion[E]]): Long = objects count (o => query forall (_ ok o))

  override def terms(query: Option[Criterion[E]], fields: Seq[Field[E, _]]): Map[Field[E, Any], Map[Any, Long]] = {
    val elements = objects.filter(o => query.forall(_ ok o))
    fields.map { field =>
      val _f = field.asInstanceOf[Field[E, Any]]
      var result = Map.empty[Any, Long]
      elements.foreach { element =>
        _f.get(element).foreach { value =>
          result = result.updated(value, result.getOrElse(value, 0l) + 1)
        }
      }
      _f -> result
    }(scala.collection.breakOut)
  }

}
