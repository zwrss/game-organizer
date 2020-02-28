package com.github.zwrss.gameorganizer.dql.parser

import com.github.zwrss.gameorganizer.dql.ast.AllAST
import com.github.zwrss.gameorganizer.dql.ast.FieldsAST
import com.github.zwrss.gameorganizer.dql.ast.LimitAST
import com.github.zwrss.gameorganizer.dql.ast.Select


/**
 * <Select>   ::= select <Selector> [where <Expression>] [order by <Sort>] [limit Limit]
 * <Selector> ::= <Fields> | "*"
 * <Fields>   ::= <ident> [, <ident>]*
 * <Limit>    ::= [<number>,] <number>
 */
trait SelectParser extends SelectorParser with CriterionParser with SortParser {

  final protected def Select: Parser[Select] =
    ("select" ~> Selector) ~
      opt("where" ~> Expression) ~
      opt("order" ~> "by" ~> Sort) ~
      opt("limit" ~> Limit) ^^ {
      case selector ~ query ~ sort ~ limit => new Select(selector, query, sort, limit)
    }

  private def Limit = opt(wholeNumber <~ ",") ~ wholeNumber ^^ {
    case offset ~ limit => LimitAST(offset.map(_.toLong), limit.toLong)
  }

}
