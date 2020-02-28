package com.github.zwrss.gameorganizer.finder

trait FieldsDescriptor[E] {

  def fields: Seq[Field[E, _]]

  def _getField(name: String): Field[Any, Any] = {
    fields.find(_.name == name).getOrElse(sys.error(s"No field with name $name")).asInstanceOf[Field[Any, Any]]
  }

}
