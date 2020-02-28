package com.github.zwrss.gameorganizer.dql

import com.github.zwrss.gameorganizer.dql.ast.CriterionAST
import com.github.zwrss.gameorganizer.dql.ast.EqualsAST
import com.github.zwrss.gameorganizer.dql.ast.ExistsAST
import com.github.zwrss.gameorganizer.dql.ast.InAST
import com.github.zwrss.gameorganizer.dql.ast.NotAST
import com.github.zwrss.gameorganizer.dql.ast.RangeAST
import com.github.zwrss.gameorganizer.dql.parser.SimpleCriterionParser
import org.scalatest.FlatSpec
import org.scalatest.Matchers

class SimpleCriterionParserSuite extends FlatSpec with Matchers {

  private val parser = new SimpleCriterionParser {

    def parse(s: String): CriterionAST = parseAll(Criterion, s) match {
      case Success(result, _) => result
      case NoSuccess(message, _) => sys.error(s"Cannot parse [$s]: " + message)
    }

  }

  import parser._

  behavior of "SimpleCriterionParser"

  it should "parse simple criteria" in {
    parse("Fuel = \"Petrol\"") shouldBe EqualsAST("Fuel", "Petrol")
    parse("Fuel in (\"Petrol\", \"Diesel\")") shouldBe InAST("Fuel", List("Petrol", "Diesel"))
    parse("Power >= 100") shouldBe RangeAST("Power", min = Option("100"))
    parse("Power <= 200") shouldBe RangeAST("Power", max = Option("200"))
    parse("Power between 100 and 200") shouldBe RangeAST("Power", Option("100"), Option("200"))
    parse("Power is null") shouldBe NotAST(ExistsAST("Power"))
    parse("Fuel is not null") shouldBe ExistsAST("Fuel")
  }

}

