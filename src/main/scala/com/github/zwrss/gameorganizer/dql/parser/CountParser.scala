package com.github.zwrss.gameorganizer.dql.parser

import com.github.zwrss.gameorganizer.dql.ast.Count


/**
 * <Count> ::= count [where <Expression>]
 */
trait CountParser extends CriterionParser {

  final protected def Count: Parser[Count] =
    "count" ~> opt("where" ~> Expression) ^^ {
      case query => new Count(query)
    }

}
