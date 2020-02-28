package com.github.zwrss.gameorganizer.dataprovider

import com.github.zwrss.gameorganizer.common.JsonHttpClient
import com.github.zwrss.gameorganizer.common.Logging
import com.github.zwrss.gameorganizer.common.ParamBuilding
import com.github.zwrss.gameorganizer.game.Game
import play.api.libs.json.JsValue

class RawgClient(http: JsonHttpClient) extends GameDataProvider with ParamBuilding with Logging {

  private val GamesUrl = "https://api.rawg.io/api/games"

  private val SearchTextParam = "search"

  private val LimitParam = "page_size"

  def getData(gameName: String): Option[Game] = {
    val game = http.get(GamesUrl, params(LimitParam -> "1", SearchTextParam -> gameName))
    val id = (game \ "results").as[Seq[JsValue]].headOption.map(o => (o \ "id").as[Long])
    id match {
      case Some(i) => getDetails(i) map (_.as[Game])
      case _ =>
        warn(s"Cannot find game with name $gameName")
        None
    }
  }

  private def getDetails(id: Long): Option[JsValue] = {
    try {
      Option(http.get(GamesUrl + "/" + id))
    } catch {
      case t: Throwable =>
        error(s"Cannot get game info about game with id $id")
        None
    }
  }

}
