package com.github.zwrss.gameorganizer.dql.parser

import com.github.zwrss.gameorganizer.dql.ast.CriterionAST

/**
 * <Criterion> ::= <Equals> | <NotEquals> | <Exists> | <Like> | <NotExists> | <In> | <Between> | <LTE> | <GTE>
 * <Equals>    ::= <ident> = <value>
 * <Like>      ::= <ident> like <value>
 * <NotEquals> ::= <ident> <> <value>
 * <Exists>    ::= <ident> is not null
 * <NotExists> ::= <ident> is null
 * <In>        ::= <ident> in (<value>[, <value>]*)
 * <Between>   ::= <ident> between <value> and <value>
 * <LTE>       ::= <ident> <= <value>
 * <GTE>       ::= <ident> >= <value>
 */
trait SimpleCriterionParser extends DqlParser {

  import com.github.zwrss.gameorganizer.dql.ast.CriterionASTExt._

  private def Value: Parser[String] = strippedStringLiteral | decimalNumber | "true" | "false"

  final protected def Criterion: Parser[CriterionAST] = Equals | NotEquals | Exists | Like | NotExists | In | Between | LTE | GTE

  private def Equals = (ident <~ "=") ~ Value ^^ {
    case field ~ value => field === value
  }

  private def Like = (ident <~ "like") ~ Value ^^ {
    case field ~ value => field like value
  }

  private def NotEquals = (ident <~ "<>") ~ Value ^^ {
    case field ~ value => !(field === value)
  }

  private def Exists = ident <~ "is" <~ "not" <~ "null" ^^ {
    case field => field.present
  }

  private def NotExists = ident <~ "is" <~ "null" ^^ {
    case field => field.missing
  }

  private def In = (ident <~ "in") ~ brackets(Value ~ rep("," ~> Value)) ^^ {
    case field ~ (v1 ~ vs) => field in (v1, vs: _*)
  }

  private def Between = (ident <~ "between") ~ (Value <~ "and") ~ Value ^^ {
    case field ~ v1 ~ v2 => field between v1 and v2
  }

  private def LTE = (ident <~ "<=") ~ Value ^^ {
    case field ~ value => field <== value
  }

  private def GTE = (ident <~ ">=") ~ Value ^^ {
    case field ~ value => field >== value
  }

}
