package com.github.zwrss.gameorganizer.dql.parser

import scala.util.parsing.combinator.JavaTokenParsers

trait DqlParser extends JavaTokenParsers {

  protected final def brackets[T](parser: => Parser[T]): Parser[T] = "(" ~> parser <~ ")"

  protected final def optBrackets[T](parser: => Parser[T]): Parser[T] = brackets(parser) | parser

  protected final def strippedStringLiteral[T]: Parser[String] = stringLiteral.map { str =>
    str.stripPrefix("\"").stripSuffix("\"")
  }

}
