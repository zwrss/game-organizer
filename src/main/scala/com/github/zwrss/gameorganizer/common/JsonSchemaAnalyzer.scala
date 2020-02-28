package com.github.zwrss.gameorganizer.common

import java.io.File

import play.api.libs.json.JsArray
import play.api.libs.json.JsBoolean
import play.api.libs.json.JsNull
import play.api.libs.json.JsNumber
import play.api.libs.json.JsObject
import play.api.libs.json.JsString
import play.api.libs.json.JsValue
import play.api.libs.json.Json

import scala.io.Source

object JsonSchemaAnalyzer {

  def analyze(json: JsValue): Tpe = json match {
    case JsArray(value) => ArrayTpe(value.map(analyze).foldLeft(Tpe.empty)(_ merge _))
    case JsObject(value) => ObjectTpe(value.map {
      case (key, value) => key -> analyze(value)
    }(scala.collection.breakOut))
    case JsNull => EmptyTpe
    case _: JsBoolean => SimpleTpe("Boolean")
    case _: JsNumber => SimpleTpe("BigDecimal")
    case _: JsString => SimpleTpe("String")
    case x => SimpleTpe(x.getClass.getSimpleName.stripSuffix("$"))
  }


  def main(args: Array[String]): Unit = {
    val gamesDir = new File("games")
    val gameJsons = gamesDir.listFiles().toList.map { gameFile =>
      val source = Source.fromFile(gameFile)
      val json = Json.parse(source.mkString)
      json
    }
    val analyzedJsons = gameJsons map analyze
    val tpe = analyzedJsons.headOption match {
      case Some(j) => analyzedJsons.tail.foldLeft(j)(_ merge _)
      case None => EmptyTpe
    }
    println(Json prettyPrint (Json parse tpe.toString))
    tpe.scalaClasses("Game") foreach println
  }

}

trait Tpe {

  def tpe: String

  final def merge(that: Tpe): Tpe = (this, that) match {
    case (OptionalTpe(a), b) => OptionalTpe.get(a merge b)
    case (a, OptionalTpe(b)) => OptionalTpe.get(a merge b)
    case (EmptyTpe, b) => OptionalTpe.get(b)
    case (a, EmptyTpe) => OptionalTpe.get(a)
    case (ObjectTpe(a), ObjectTpe(b)) =>
      val keys = a.keySet ++ b.keySet
      val newMap: Map[String, Tpe] = keys.map { key =>
        val va = a.getOrElse(key, EmptyTpe)
        val vb = b.getOrElse(key, EmptyTpe)
        val value = va merge vb
        key -> value
      }(scala.collection.breakOut)
      ObjectTpe(newMap)
    case (ArrayTpe(a), ArrayTpe(b)) => ArrayTpe(a merge b)
    case (a: SimpleTpe, b: Mixed) => Mixed(b.tpes + a)
    case (a: Mixed, b: SimpleTpe) => Mixed(a.tpes + b)
    case (a, b) => Mixed(a, b)
  }

  override def toString: String = "\"" + tpe + "\""

  def scalaClasses(fieldName: String): Seq[String] = Seq.empty

  def scalaTpe(fieldName: String = "Root"): String = tpe

}

object Tpe {
  def empty: Tpe = EmptyTpe
}

case class SimpleTpe(tpe: String) extends Tpe

case class Mixed(tpes: Set[Tpe]) extends Tpe {
  override def tpe: String = tpes.map(_.tpe).mkString(",")

  override def scalaTpe(fieldName: String): String = "Any"
}

object Mixed {
  def apply(tpes: Tpe*): Tpe = {
    val t = tpes.toSet
    if (t.isEmpty) EmptyTpe
    else if (t.size == 1) t.head
    else new Mixed(t)
  }
}

case class ArrayTpe(inner: Tpe) extends Tpe {

  override def tpe: String = "Array"

  override def toString: String = s"""{ "$tpe": $inner }"""

  override def scalaTpe(fieldName: String): String = s"List[${inner.scalaTpe(fieldName)}]"

  override def scalaClasses(fieldName: String): Seq[String] = inner.scalaClasses(fieldName)
}

case class ObjectTpe(fields: Map[String, Tpe]) extends Tpe {

  private def fieldsMapping: String = fields.map {
    case (key, tpe) => s"""  "$key": $tpe  """
  }.mkString(",\n")

  override def toString: String =
    s"""|{
        |  $fieldsMapping
        |}""".stripMargin

  override def tpe: String = "Object"

  override def scalaTpe(fieldName: String): String = fieldName.split("[ _]").map(_.capitalize).mkString

  override def scalaClasses(fieldName: String): Seq[String] = {
    val elems = fields.map {
      case (key, value) =>
        val fieldName = if (!key.headOption.exists(_.isLetter)) s"_$key" else key
        s"$fieldName: ${value.scalaTpe(key)}"
    }
    val tName = scalaTpe(fieldName)
    Seq(
      s"case class $tName(${elems.mkString(", ")})",
      s"object $tName { implicit def format: Format[$tName] = Json.using[WithDefaultValues].format }"
    ) ++ fields.toSeq.flatMap {
      case (key, value) => value.scalaClasses(key)
    }.distinct
  }
}

case class OptionalTpe(inner: Tpe) extends Tpe {
  override def tpe: String = "Option"

  override def toString: String = s"""{ "$tpe": $inner }"""

  override def scalaClasses(fieldName: String): Seq[String] = inner.scalaClasses(fieldName)

  override def scalaTpe(fieldName: String): String = s"Option[${inner.scalaTpe(fieldName)}]"
}

object OptionalTpe {
  def get(tpe: Tpe): Tpe = tpe match {
    case EmptyTpe => EmptyTpe
    case t: OptionalTpe => t
    case t => OptionalTpe(t)
  }
}

object EmptyTpe extends Tpe {
  override def tpe: String = "Null"

  override def scalaTpe(fieldName: String): String = "Nothing"
}