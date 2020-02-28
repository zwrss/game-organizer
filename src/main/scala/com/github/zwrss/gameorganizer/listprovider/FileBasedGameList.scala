package com.github.zwrss.gameorganizer.listprovider

import java.io.File

import scala.io.Source

@deprecated
abstract class FileBasedGameList(fileName: String) {

  private val file = new File("cache", fileName)

  def get(): Seq[String] = {
    val source = Source.fromFile(file)
    try {
      source.getLines().toList
    } finally {
      source.close()
    }
  }

}
