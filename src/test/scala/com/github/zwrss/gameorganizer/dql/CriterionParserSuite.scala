package com.github.zwrss.gameorganizer.dql

import com.github.zwrss.gameorganizer.dql.ast.AndAST
import com.github.zwrss.gameorganizer.dql.ast.CriterionAST
import com.github.zwrss.gameorganizer.dql.ast.EqualsAST
import com.github.zwrss.gameorganizer.dql.ast.InAST
import com.github.zwrss.gameorganizer.dql.ast.LikeAST
import com.github.zwrss.gameorganizer.dql.ast.OrAST
import com.github.zwrss.gameorganizer.dql.ast.RangeAST
import com.github.zwrss.gameorganizer.dql.parser.CriterionParser
import org.scalatest.FlatSpec
import org.scalatest.Matchers

class CriterionParserSuite extends FlatSpec with Matchers {

  private val criterionParser = new CriterionParser {
    def toCriterion(s: String): CriterionAST = parseAll(Expression, s) match {
      case Success(result, _) => result
      case NoSuccess(message, _) => sys.error(s"Cannot parse [$s]: " + message)
    }
  }

  import criterionParser._

  behavior of "CriterionParser"

  it should "parse simple criteria" in {
    toCriterion("Fuel = \"Petrol\"") shouldBe EqualsAST("Fuel", "Petrol")
    toCriterion("Fuel in (\"Petrol\", \"Diesel\")") shouldBe InAST("Fuel", List("Petrol", "Diesel"))
    toCriterion("Power >= 100") shouldBe RangeAST("Power", min = Option("100"))
    toCriterion("Power <= 200") shouldBe RangeAST("Power", max = Option("200"))
    toCriterion("Power between 100 and 200") shouldBe RangeAST("Power", Option("100"), Option("200"))
    toCriterion("Fuel like \".*ol\"") shouldBe LikeAST("Fuel", ".*ol")
  }

  it should "parse 'and' and 'or'" in {

    val A = EqualsAST("BodyType", "Small")
    val a = "BodyType = \"Small\""
    val B = EqualsAST("Fuel", "Petrol")
    val b = "Fuel = \"Petrol\""
    val C = EqualsAST("Drive", "FrontWheel")
    val c = "Drive = \"FrontWheel\""
    val D = EqualsAST("Transmission", "Automatic")
    val d = "Transmission = \"Automatic\""

    toCriterion(s"$a and $b") shouldBe A && B

    toCriterion(s"$a or $b") shouldBe A || B

    // just in case
    A && B || C shouldBe OrAST(AndAST(A, B), C)
    A && B || C shouldBe (A && B) || C

    toCriterion(s"$a and $b or $c") shouldBe A && B || C

    toCriterion(s"$a and ($b or $c)") shouldBe A && (B || C)

    toCriterion(s"($a and $b) or $c") shouldBe (A && B) || C

    A || B && C shouldBe A || (B && C) // just in case
    toCriterion(s"$a or $b and $c") shouldBe A || B && C

    toCriterion(s"$a or ($b and $c)") shouldBe A || (B && C)

    toCriterion(s"($a or $b) and $c") shouldBe (A || B) && C

    A && B || C && D shouldBe (A && B) || (C && D) // just in case
    toCriterion(s"$a and $b or $c and $d") shouldBe A && B || C && D

    A && (B || C) && D shouldBe (A && (B || C)) && D // just in case
    A && (B || C) && D shouldBe AndAST(AndAST(A, OrAST(B, C)), D)
    toCriterion(s"$a and ($b or $c) and $d") shouldBe A && (B || C) && D

  }

}

