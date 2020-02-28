package com.github.zwrss.gameorganizer.dql.ast

trait SelectorAST

case object AllAST extends SelectorAST

case class FieldsAST(fields: Seq[String]) extends SelectorAST