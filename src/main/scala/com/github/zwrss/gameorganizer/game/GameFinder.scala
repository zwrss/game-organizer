package com.github.zwrss.gameorganizer.game

import java.io.File

import com.github.zwrss.gameorganizer.common.Logging
import com.github.zwrss.gameorganizer.finder.BigDecimalField
import com.github.zwrss.gameorganizer.finder.Field
import com.github.zwrss.gameorganizer.finder.FieldsDescriptor
import com.github.zwrss.gameorganizer.finder.FileCachingMemoryFinder
import com.github.zwrss.gameorganizer.finder.IterableField
import com.github.zwrss.gameorganizer.finder.OptionalField
import com.github.zwrss.gameorganizer.finder.SimpleField
import com.github.zwrss.gameorganizer.finder.StringField

class GameFinder extends FileCachingMemoryFinder[BigDecimal, Game] with FieldsDescriptor[Game] {

  override protected def cacheDirectory: File = new File("cache/games")

  override def getId(e: Game): BigDecimal = e.details.id

  val id = new SimpleField[Game, BigDecimal]("id", _.details.id) with BigDecimalField[Game]
  val name = new SimpleField[Game, String]("name", _.details.name) with StringField[Game]
  val genres = new IterableField[Game, String]("genres", _.details.genres.map(_.name)) with StringField[Game]
  val tags = new IterableField[Game, String]("tags", _.details.tags.map(_.name)) with StringField[Game]
  val rating = new SimpleField[Game, BigDecimal]("rating", _.community.rating) with BigDecimalField[Game]
  val metacritic = new OptionalField[Game, BigDecimal]("metacritic", _.community.metacritic) with BigDecimalField[Game]
  val released = new OptionalField[Game, String]("released", _.details.released) with StringField[Game]

  override def fields: Seq[Field[Game, _]] = Seq(id, name, genres, tags, rating, metacritic, released)

}

object GameFinder extends Logging {

  lazy val get: GameFinder = new GameFinder

}