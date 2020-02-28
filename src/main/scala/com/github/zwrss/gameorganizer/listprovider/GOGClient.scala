package com.github.zwrss.gameorganizer.listprovider

import com.github.zwrss.gameorganizer.common.JsonHttpClient
import com.github.zwrss.gameorganizer.common.ParamBuilding

@deprecated
object GOGClient extends ParamBuilding {

  private val GAMES_URL = "https://embed.gog.com/user/data/games"

  def get()(implicit http: JsonHttpClient): Seq[String] = {
    val response = http.get(GAMES_URL)
    (response \ "owned").as[Seq[Int]].toStream.flatMap { id =>
      getName(id)
    }
  }

  private val GAME_URL = "https://api.gog.com/products/"

  private val LOCALE_PARAM = "locale"

  private def getName(id: Int)(implicit http: JsonHttpClient): Option[String] = try {
    val response = http.get(GAME_URL + id, params(LOCALE_PARAM -> "en-GB"))
    val name = (response \ "title").as[String]
    Option(name)
  } catch {
    case t: Throwable =>
      println(s"Cannot get name for game with id $id")
      None
  }

}