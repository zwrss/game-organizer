package com.github.zwrss.gameorganizer.common

import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter

import scala.io.Source

abstract class FileCache[K, V](cacheDirectory: File, fileExtension: Option[String] = None) extends SyncCache[K, V] {

  def valueToString(v: V): String

  def keyToString(k: K): String

  def valueFromString(v: String): V


  private def ensuredCacheDirectory: File = {
    if (!cacheDirectory.exists()) cacheDirectory.mkdir()
    cacheDirectory
  }

  private def getFile(id: String): File = {
    val filename = id.replaceAll("[^A-Za-z0-9]", "_") + fileExtension.getOrElse("")
    new File(ensuredCacheDirectory, filename)
  }

  private def toCache(id: String, value: V): Unit = {
    val cacheFile = getFile(id)
    val writer = new BufferedWriter(new FileWriter(cacheFile))
    try {
      writer write valueToString(value)
    } finally {
      writer.flush()
      writer.close()
    }
  }

  private def fromCache(id: String)(loader: => V): V = {
    val cacheFile = getFile(id)
    if (cacheFile.exists()) {
      val source = Source.fromFile(cacheFile)
      try {
        valueFromString(source.getLines().mkString(""))
      } finally {
        source.close()
      }
    } else {
      val loaded: V = loader
      toCache(id, loaded)
      loaded
    }
  }

  override def load(key: K)(loader: => V): V = {
    fromCache(keyToString(key))(loader)
  }

  def clear(): Unit = {
    ensuredCacheDirectory.listFiles().foreach(_.delete())
  }

  override def clear(key: K): Unit = {
    getFile(keyToString(key)).delete()
  }
}
