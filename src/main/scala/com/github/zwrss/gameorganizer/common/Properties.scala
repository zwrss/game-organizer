package com.github.zwrss.gameorganizer.common

import scala.io.Source

//todo better impl
object Properties {
  def getProperty(name: String): Option[String] = {
    val src = Source.fromFile("zwrss.properties")
    try {
      src.getLines().collectFirst {
        case line if line.split('=').map(_.trim).headOption == Option(name) =>
          line.split('=').map(_.trim).lastOption
      }.flatten
    } finally {
      src.close()
    }
  }
}
