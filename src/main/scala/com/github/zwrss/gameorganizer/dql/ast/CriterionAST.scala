package com.github.zwrss.gameorganizer.dql.ast

import com.github.zwrss.gameorganizer.finder
import com.github.zwrss.gameorganizer.finder.And
import com.github.zwrss.gameorganizer.finder.Criterion
import com.github.zwrss.gameorganizer.finder.Equals
import com.github.zwrss.gameorganizer.finder.Exists
import com.github.zwrss.gameorganizer.finder.FieldsDescriptor
import com.github.zwrss.gameorganizer.finder.In
import com.github.zwrss.gameorganizer.finder.Like
import com.github.zwrss.gameorganizer.finder.Not
import com.github.zwrss.gameorganizer.finder.Or


trait CriterionAST {
  def toQuery(f: FieldsDescriptor[_]): Criterion[Any]

  def &&(that: CriterionAST): AndAST = AndAST(this, that)

  def ||(that: CriterionAST): OrAST = OrAST(this, that)

  def unary_! : NotAST = NotAST(this)
}

case class CriterionASTExt(str: String) extends AnyVal {
  def ===(value: String) = EqualsAST(str, value)

  def like(value: String) = LikeAST(str, value)

  def in(value: String, values: String*) = InAST(str, value :: values.toList)

  def <==(value: String) = RangeAST(str, max = Option(value))

  def >==(value: String) = RangeAST(str, min = Option(value))

  def between(min: String) = BetweenCriterionASTExt(str, min)

  def missing = !present

  def present = ExistsAST(str)
}

object CriterionASTExt {
  implicit def toCriterionExt(str: String): CriterionASTExt = new CriterionASTExt(str)
}

case class BetweenCriterionASTExt(str: String, min: String) {
  def and(max: String): RangeAST = RangeAST(str, Option(min), Option(max))
}

case class AndAST(q1: CriterionAST, q2: CriterionAST) extends CriterionAST {
  override def toQuery(f: FieldsDescriptor[_]): Criterion[Any] = And(q1 toQuery f, q2 toQuery f)
}

case class OrAST(q1: CriterionAST, q2: CriterionAST) extends CriterionAST {
  override def toQuery(f: FieldsDescriptor[_]): Criterion[Any] = Or(q1 toQuery f, q2 toQuery f)
}

case class NotAST(q: CriterionAST) extends CriterionAST {
  override def toQuery(f: FieldsDescriptor[_]): Criterion[Any] = Not(q toQuery f)
}

case class ExistsAST(field: String) extends CriterionAST {
  override def toQuery(f: FieldsDescriptor[_]): Criterion[Any] = Exists(f _getField field)
}

case class EqualsAST(field: String, value: String) extends CriterionAST {
  override def toQuery(f: FieldsDescriptor[_]): Criterion[Any] = {
    val _field = f _getField field
    Equals(_field, _field deserialize value)
  }
}

case class LikeAST(field: String, value: String) extends CriterionAST {
  override def toQuery(f: FieldsDescriptor[_]): Criterion[Any] = {
    val _field = f _getField field
    Like(_field, _field deserialize value)
  }
}

case class InAST(field: String, values: Seq[String]) extends CriterionAST {
  override def toQuery(f: FieldsDescriptor[_]): Criterion[Any] = {
    val _field = f _getField field
    In(_field, values map _field.deserialize)
  }
}

case class RangeAST(field: String, min: Option[String] = None, max: Option[String] = None) extends CriterionAST {
  override def toQuery(f: FieldsDescriptor[_]): Criterion[Any] = {
    val _field = f _getField field
    finder.Range(_field, min map _field.deserialize, max map _field.deserialize)
  }
}