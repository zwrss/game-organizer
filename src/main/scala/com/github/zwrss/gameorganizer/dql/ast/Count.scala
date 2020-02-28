package com.github.zwrss.gameorganizer.dql.ast

import com.github.zwrss.gameorganizer.finder.FieldsDescriptor
import com.github.zwrss.gameorganizer.finder.Finder

case class Count(where: Option[CriterionAST] = None) extends DqlCommand {
  override def execute[E](source: Finder[_, E] with FieldsDescriptor[E]): DqlCommandResult = {
    val query = where map (_ toQuery source)
    val count = source.asInstanceOf[Finder[Any, Any]].count(query)
    DqlCommandResult.simple("count" -> count)
  }
}

