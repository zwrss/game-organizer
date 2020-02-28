package com.github.zwrss.gameorganizer.dql.parser

import com.github.zwrss.gameorganizer.dql.ast.SimpleSortAST
import com.github.zwrss.gameorganizer.dql.ast.SortAST

/**
 * <Sort>       ::= <SimpleSort> [, <SimpleSort>]*
 * <SimpleSort> ::= <ident> [desc]
 */
trait SortParser extends DqlParser {

  final protected def Sort: Parser[SortAST] = SimpleSort ~ rep("," ~> SimpleSort) ^^ {
    case s1 ~ ss => (s1 /: ss) (_ and _)
  }

  private def SimpleSort: Parser[SortAST] = ident ~ opt("desc") ^^ {
    case f ~ desc => SimpleSortAST(f, desc.isEmpty)
  }

}
