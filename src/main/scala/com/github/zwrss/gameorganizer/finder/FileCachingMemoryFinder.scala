package com.github.zwrss.gameorganizer.finder

import java.io.File

import com.github.zwrss.gameorganizer.common.FileCache
import play.api.libs.json.Format
import play.api.libs.json.Json

abstract class FileCachingMemoryFinder[Id: Format, E: Format] extends SimpleMemoryFinder[Id, E] {

  protected def cacheDirectory: File

  private val idsCache = new FileCache[Unit, Seq[Id]](cacheDirectory, fileExtension = Option(".ids")) {
    override def valueToString(v: Seq[Id]): String = (Json toJson v).toString

    override def keyToString(k: Unit): String = "all"

    override def valueFromString(v: String): Seq[Id] = (Json parse v).as[Seq[Id]]
  }

  private var _ids = idsCache.load({})(Seq.empty[Id])

  private val cache = new FileCache[Id, E](cacheDirectory, fileExtension = Option(".obj")) {
    override def valueToString(v: E): String = (Json toJson v).toString

    override def keyToString(k: Id): String = (Json toJson k).toString

    override def valueFromString(v: String): E = (Json parse v).as[E]
  }

  private var _objects = _ids.map(id => cache.load(id)(sys.error(s"Cannot find element $id in cache")))

  final override def objects: Seq[E] = _objects

  final def insert(element: E): Unit = insert(Seq(element))

  final def insert(elements: Seq[E]): Unit = {
    // inserting elements
    _objects = _objects ++ elements
    elements.foreach { element =>
      cache.load(getId(element))(element)
    }
    // updating ids
    _ids = _ids ++ elements.map(getId)
    idsCache.clear({})
    idsCache.load({})(_ids)
  }

  final def clear(): Unit = {
    _ids = Seq.empty
    idsCache.clear()
    _objects = Seq.empty
    cache.clear()
  }

}
