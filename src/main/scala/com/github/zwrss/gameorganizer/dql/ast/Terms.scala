package com.github.zwrss.gameorganizer.dql.ast

import com.github.zwrss.gameorganizer.finder.Criterion
import com.github.zwrss.gameorganizer.finder.Field
import com.github.zwrss.gameorganizer.finder.FieldsDescriptor
import com.github.zwrss.gameorganizer.finder.Finder

case class Terms(what: SelectorAST, where: Option[CriterionAST] = None) extends DqlCommand {
  override def execute[E](source: Finder[_, E] with FieldsDescriptor[E]): DqlCommandResult = {
    val fields: Seq[Field[Any, Any]] = what match {
      case AllAST => source.fields.sortBy(_.name).map(_.asInstanceOf[Field[Any, Any]])
      case FieldsAST(fields) => fields.map(source._getField)
    }

    val query: Option[Criterion[E]] = where.map(_.toQuery(source).asInstanceOf[Criterion[E]])

    val terms = source.terms(query, fields.map(_.asInstanceOf[Field[E, _]])).map {
      case (field, values) => field -> values.toList
    }.toList

    val allValues = terms.flatMap {
      case (field, values) => values.sortBy(-_._2).map {
        case (value, count) => List(field.name, field serialize value, count.toString)
      }
    }

    DqlCommandResult(List("field", "value", "count"), allValues)

  }
}

