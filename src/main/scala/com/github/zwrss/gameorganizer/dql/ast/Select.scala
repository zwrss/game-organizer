package com.github.zwrss.gameorganizer.dql.ast

import com.github.zwrss.gameorganizer.finder.Criterion
import com.github.zwrss.gameorganizer.finder.Field
import com.github.zwrss.gameorganizer.finder.FieldsDescriptor
import com.github.zwrss.gameorganizer.finder.Finder
import com.github.zwrss.gameorganizer.finder.Sort

case class Select(what: SelectorAST, where: Option[CriterionAST] = None, sort: Option[SortAST] = None, limit: Option[LimitAST] = None) extends DqlCommand {
  override def execute[E](source: Finder[_, E] with FieldsDescriptor[E]): DqlCommandResult = {

    val fields: Seq[Field[Any, Any]] = what match {
      case AllAST => source.fields.sortBy(_.name).map(_.asInstanceOf[Field[Any, Any]])
      case FieldsAST(fields) => fields.map(source._getField)
    }

    val query: Option[Criterion[E]] = where.map(_.toQuery(source).asInstanceOf[Criterion[E]])

    val sorts: Option[Sort[E]] = sort.map(_.toSort(source).asInstanceOf[Sort[E]])

    val results = source.find(query, sorts, limit.flatMap(_.offset) getOrElse 0, limit.map(_.limit) getOrElse 10)

    DqlCommandResult(fields.map(_.name).toList, results.map { o =>
      fields.map { field =>
        field.get(o).map(field.serialize).mkString(", ")
      }.toList
    }.toList)

  }
}

case class LimitAST(offset: Option[Long], limit: Long)

