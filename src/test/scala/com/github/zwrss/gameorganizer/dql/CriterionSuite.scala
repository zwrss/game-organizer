package com.github.zwrss.gameorganizer.dql

import org.scalatest.FlatSpec
import org.scalatest.Matchers

class CriterionSuite extends FlatSpec with Matchers {

  import com.github.zwrss.gameorganizer.dql.ast.CriterionASTExt._

  val A = "Fuel" === "Petrol"
  val B = "Fuel" === "LPG"
  val C = "Fuel" === "Hybrid"
  val D = "Fuel" === "CNG"
  val E = "Fuel" === "Electric"
  val F = "Fuel" === "Diesel"
  val G = "Fuel" in("Petrol", "LPG")

  behavior of "Criteria"

  it should "and is commutative" in {
    A && B shouldBe B && A
    (A && (B && C)) shouldBe (C && (A && B))
  }

  it should "or is commutative" in {
    A || B shouldBe B || A
    (A || (B || C)) shouldBe (C || (A || B))
  }


}
