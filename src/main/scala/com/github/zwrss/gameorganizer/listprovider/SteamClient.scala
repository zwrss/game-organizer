package com.github.zwrss.gameorganizer.listprovider

import com.github.zwrss.gameorganizer.common.JsonHttpClient
import com.github.zwrss.gameorganizer.common.Logging
import com.github.zwrss.gameorganizer.common.ParamBuilding
import play.api.libs.json.JsObject
import play.api.libs.json.JsValue

class SteamClient(http: JsonHttpClient, val SteamKey: String, val SteamId: String) extends GameListProvider with ParamBuilding with Logging {

  private val SteamOwnedGamesUrl = "http://api.steampowered.com/IPlayerService/GetOwnedGames/v0001/"

  private val SteamIdParam = "steamid"

  private val SteamKeyParam = "key"

  private val FormatParam = "format"

  def getGameList: Seq[String] = {
    val response = http.get(SteamOwnedGamesUrl, params(SteamKeyParam -> SteamKey, SteamIdParam -> SteamId, FormatParam -> "json"))
    //    val appIDs = (response \ "response" \ "games" \ "appid").as[List[String]]
    val games = (response \ "response" \ "games").as[Seq[JsValue]]
    val appIDs = games.map { obj =>
      (obj \ "appid").as[Int]
    }
    getFromAppList(appIDs)
  }

  private val SteamGameSchemaUrl = "http://api.steampowered.com/ISteamUserStats/GetSchemaForGame/v2/"

  private val AppIdParam = "appid"

  private def getSchema(appid: String): Option[String] = {
    val response = http.get(SteamGameSchemaUrl, params(SteamKeyParam -> SteamKey, AppIdParam -> appid, FormatParam -> "json"))
    val opt = (response \ "game" \ "gameName").asOpt[String]
    opt match {
      case Some(x) if x.trim.isEmpty || x.startsWith("ValveTestApp") =>
        None
      case x => x
    }
  }

  private val SteamAppListUrl = "https://api.steampowered.com/ISteamApps/GetAppList/v2/"

  private def getFromAppList(appids: Seq[Int]): Seq[String] = {
    val response = http.get(SteamAppListUrl, params(FormatParam -> "json"))

    def search(left: Seq[JsObject], toFind: Set[Int], found: Map[Int, String] = Map.empty): Map[Int, String] = {
      if (left.isEmpty) found
      else if (toFind.isEmpty) found
      else {
        val tuple = left.head
        val id = (tuple \ "appid").as[Int]
        if (toFind(id)) {
          val newFound = found + (id -> (tuple \ "name").as[String])
          val newToFind = toFind - id
          search(left.tail, newToFind, newFound)
        } else {
          search(left.tail, toFind, found)
        }
      }
    }

    val map = search((response \ "applist" \ "apps").as[Seq[JsObject]], appids.toSet)

    appids flatMap map.get
  }

}
