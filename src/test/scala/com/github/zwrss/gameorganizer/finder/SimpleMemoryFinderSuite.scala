package com.github.zwrss.gameorganizer.finder

import org.scalatest.FlatSpec
import org.scalatest.Matchers

class SimpleMemoryFinderSuite extends FlatSpec with Matchers {


  behavior of "SimpleMemoryFinder"

  case class Box(id: String, label: String, flag: Boolean, optDecimal: Option[BigDecimal], list: List[String])

  def getFinder(_objs: Box*) = new SimpleMemoryFinder[String, Box] {

    override def objects: Seq[Box] = _objs

    override def getId(e: Box): String = e.id

    val label = new SimpleField[Box, String]("label", _.label) with StringField[Box]

    val flag = new SimpleField[Box, Boolean]("flag", _.flag) with BooleanField[Box]

    val optDecimal = new OptionalField[Box, BigDecimal]("optDecimal", _.optDecimal) with BigDecimalField[Box]

    val list = new IterableField[Box, String]("list", _.list) with StringField[Box]

  }


  it should "find elements by id" in {

    val box1 = Box("1", "first label", true, None, Nil)
    val box2 = Box("2", "second label", false, None, Nil)

    val finder = getFinder(box1, box2)

    finder.get("1") shouldBe box1
    finder.get("2") shouldBe box2

  }

  it should "find elements by fields" in {

    val box1 = Box("1", "first label", true, None, Nil)
    val box2 = Box("2", "second label", false, Option(BigDecimal(5)), Nil)
    val box3 = Box("3", "third label", false, None, List("a", "c"))
    val box4 = Box("4", "fourth label", true, Option(BigDecimal(10)), List("b"))
    val box5 = Box("5", "fifth label", true, None, List("d"))

    val finder = getFinder(box1, box2, box3, box4, box5)

    finder.find(query = Option(finder.label === "first label")) should contain theSameElementsAs Seq(box1)
    finder.find(query = Option(finder.optDecimal.missing)) should contain theSameElementsAs Seq(box1, box3, box5)
    finder.find(query = Option(finder.optDecimal.present)) should contain theSameElementsAs Seq(box2, box4)
    finder.find(query = Option(finder.optDecimal.present && finder.list.present)) should contain theSameElementsAs Seq(box4)
    finder.find(query = Option(finder.optDecimal between BigDecimal(4) and BigDecimal(6))) should contain theSameElementsAs Seq(box2)

    {
      val result = finder.find(sort = Option(finder.optDecimal.asc))
      result.take(2) should contain theSameElementsInOrderAs Seq(box2, box4)
      result should contain theSameElementsAs Seq(box1, box2, box3, box4, box5)
    }

    finder.find(sort = Option(finder.optDecimal.asc and finder.label.asc)) should contain theSameElementsInOrderAs Seq(box2, box4, box5, box1, box3)

  }

  it should "count elements" in {

    val box1 = Box("1", "first label", true, None, Nil)
    val box2 = Box("2", "second label", false, Option(BigDecimal(5)), Nil)
    val box3 = Box("3", "third label", false, None, List("a", "c"))
    val box4 = Box("4", "fourth label", true, Option(BigDecimal(10)), List("b"))
    val box5 = Box("5", "fifth label", true, None, List("d"))

    val finder = getFinder(box1, box2, box3, box4, box5)

    finder.count(query = Option(finder.label === "first label")) shouldBe 1
    finder.count(query = Option(finder.optDecimal.missing)) shouldBe 3
    finder.count(query = Option(finder.optDecimal.present)) shouldBe 2
    finder.count(query = Option(finder.optDecimal.present && finder.list.present)) shouldBe 1
    finder.count(query = Option(finder.optDecimal between BigDecimal(4) and BigDecimal(6))) shouldBe 1

  }

  it should "calculate terms" in {

    val box1 = Box("1", "first label", true, None, Nil)
    val box2 = Box("2", "second label", false, Option(BigDecimal(5)), Nil)
    val box3 = Box("3", "third label", false, None, List("a", "c"))
    val box4 = Box("4", "fourth label", true, Option(BigDecimal(10)), List("b"))
    val box5 = Box("5", "fifth label", true, None, List("c", "d"))

    val finder = getFinder(box1, box2, box3, box4, box5)

    finder.terms(None, Seq(finder.optDecimal, finder.flag, finder.list)) shouldBe Map(
      finder.optDecimal -> Map(
        BigDecimal(5) -> 1L,
        BigDecimal(10) -> 1L
      ),
      finder.flag -> Map(
        true -> 3L,
        false -> 2L
      ),
      finder.list -> Map(
        "a" -> 1L,
        "b" -> 1L,
        "c" -> 2L,
        "d" -> 1L
      )
    )

  }

}
