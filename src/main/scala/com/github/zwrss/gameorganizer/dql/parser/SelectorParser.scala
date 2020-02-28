package com.github.zwrss.gameorganizer.dql.parser

import com.github.zwrss.gameorganizer.dql.ast.AllAST
import com.github.zwrss.gameorganizer.dql.ast.FieldsAST
import com.github.zwrss.gameorganizer.dql.ast.SelectorAST

trait SelectorParser extends DqlParser {

  protected def Selector: Parser[SelectorAST] = Fields | ("*" ^^^ AllAST)

  private def Fields = ident ~ rep("," ~> ident) ^^ {
    case f1 ~ fs => FieldsAST(f1 :: fs)
  }

}
