package com.github.zwrss.gameorganizer.dql.ast

import com.github.zwrss.gameorganizer.dql.parser.CountParser
import com.github.zwrss.gameorganizer.dql.parser.SelectParser
import com.github.zwrss.gameorganizer.dql.parser.TermsParser
import com.github.zwrss.gameorganizer.finder.FieldsDescriptor
import com.github.zwrss.gameorganizer.finder.Finder

trait DqlCommand {
  def execute[E](source: Finder[_, E] with FieldsDescriptor[E]): DqlCommandResult
}

object DqlCommand {

  private val parser = new SelectParser with TermsParser with CountParser {
    def parse(dql: String): DqlCommand = parseAll(Select | Terms | Count, dql) match {
      case Success(result, _) => result
      case NoSuccess(message, _) => sys.error(s"Cannot parse [$dql]: " + message)
    }
  }

  def fromDql(dql: String): DqlCommand = parser parse dql
}

case class DqlCommandResult(headers: List[String], values: List[List[String]])

object DqlCommandResult {
  def simple(values: (String, Any)*): DqlCommandResult = {
    new DqlCommandResult(values.map(_._1).toList, List(values.map(_._2.toString).toList))
  }
}