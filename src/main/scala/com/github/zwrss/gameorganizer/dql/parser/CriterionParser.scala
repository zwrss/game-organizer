package com.github.zwrss.gameorganizer.dql.parser

import com.github.zwrss.gameorganizer.dql.ast.CriterionAST

trait CriterionParser extends SimpleCriterionParser with LogicalExpressionParser {

  final override protected def Variable: Parser[CriterionAST] = Criterion

}

