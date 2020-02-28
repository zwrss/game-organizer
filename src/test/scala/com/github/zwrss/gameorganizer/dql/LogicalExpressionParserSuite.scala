package com.github.zwrss.gameorganizer.dql

import com.github.zwrss.gameorganizer.dql.ast.AndAST
import com.github.zwrss.gameorganizer.dql.ast.CriterionAST
import com.github.zwrss.gameorganizer.dql.ast.EqualsAST
import com.github.zwrss.gameorganizer.dql.ast.OrAST
import com.github.zwrss.gameorganizer.dql.parser.LogicalExpressionParser
import org.scalatest.FlatSpec
import org.scalatest.Matchers

class LogicalExpressionParserSuite extends FlatSpec with Matchers {

  private val parser = new LogicalExpressionParser {
    
    override protected def Variable: Parser[CriterionAST] = ident ^^ (x => EqualsAST(x, x))

    def parse(s: String): CriterionAST = parseAll(Expression, s) match {
      case Success(result, _) => result
      case NoSuccess(message, _) => sys.error(s"Cannot parse [$s]: " + message)
    }
    
  }

  import parser._

  behavior of "LogicalExpressionParser"

  it should "parse 'and' and 'or'" in {

    val A = EqualsAST("A", "A")
    val B = EqualsAST("B", "B")
    val C = EqualsAST("C", "C")
    val D = EqualsAST("D", "D")

    parse("A and B") shouldBe A && B

    parse("A or B") shouldBe A || B

    // just in case
    A && B || C shouldBe OrAST(AndAST(A, B), C)
    A && B || C shouldBe (A && B) || C

    parse("A and B or C") shouldBe A && B || C

    parse("A and (B or C)") shouldBe A && (B || C)

    parse("(A and B) or C") shouldBe (A && B) || C

    A || B && C shouldBe A || (B && C) // just in case
    parse("A or B and C") shouldBe A || B && C

    parse("A or (B and C)") shouldBe A || (B && C)

    parse("(A or B) and C") shouldBe (A || B) && C

    A && B || C && D shouldBe (A && B) || (C && D) // just in case
    parse("A and B or C and D") shouldBe A && B || C && D

    A && (B || C) && D shouldBe (A && (B || C)) && D // just in case
    A && (B || C) && D shouldBe AndAST(AndAST(A, OrAST(B, C)), D)
    parse("A and (B or C) and D") shouldBe A && (B || C) && D

  }

}

