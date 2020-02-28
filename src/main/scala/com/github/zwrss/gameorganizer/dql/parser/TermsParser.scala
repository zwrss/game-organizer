package com.github.zwrss.gameorganizer.dql.parser

import com.github.zwrss.gameorganizer.dql.ast.Terms


/**
 * <Terms> ::= count [where <Expression>]
 */
trait TermsParser extends SelectorParser with CriterionParser {

  final protected def Terms: Parser[Terms] =
    ("terms" ~> Selector) ~
      opt("where" ~> Expression) ^^ {
      case selector ~ query => new Terms(selector, query)
    }

}
