package com.github.zwrss.gameorganizer.common

trait ParamBuilding {

  protected def params(tuples: (String, String)*): Map[String, List[String]] = tuples.map {
    case (key, value) => key -> List(value)
  }(scala.collection.breakOut)

}