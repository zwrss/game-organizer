package com.github.zwrss.gameorganizer.game

import play.api.libs.json.Format
import play.api.libs.json.JsError
import play.api.libs.json.JsNumber
import play.api.libs.json.JsObject
import play.api.libs.json.JsPath
import play.api.libs.json.JsResult
import play.api.libs.json.JsSuccess
import play.api.libs.json.JsValue
import play.api.libs.json.Json
import play.api.libs.json.Json.WithDefaultValues
import play.api.libs.json.Reads
import play.api.libs.json.Writes

case class Game(
  details: GameDetails,
  parent: ParentInfo,
  media: MediaInfo,
  community: CommunityInfo
)

object Game {

  private val _reads: Reads[Game] = json => for {
    details <- GameDetails.format.reads(json)
    parent <- ParentInfo.format.reads(json)
    media <- MediaInfo.format.reads(json)
    community <- CommunityInfo.format.reads(json)
  } yield {
    Game(details, parent, media, community)
  }

  private val _writes: Writes[Game] = o => {
    val details = (GameDetails.format writes o.details).as[JsObject]
    val parent = (ParentInfo.format writes o.parent).as[JsObject]
    val media = (MediaInfo.format writes o.media).as[JsObject]
    val community = (CommunityInfo.format writes o.community).as[JsObject]
    details deepMerge parent deepMerge media deepMerge community
  }

  implicit val format: Format[Game] = new Format[Game] {
    override def reads(json: JsValue): JsResult[Game] = _reads reads json

    override def writes(o: Game): JsValue = _writes writes o
  }

}


// hacked

case class GameDetails(
  achievements_count: BigDecimal,
  added_by_status: Option[AddedByStatus],
  added: BigDecimal,
  alternative_names: List[String],
  creators_count: BigDecimal,
  description_raw: String,
  description: String,
  developers: List[Developer],
  esrb_rating: Option[EsrbRating],
  genres: List[Genres],
  id: BigDecimal,
  name_original: String,
  name: String,
  platforms: List[Platform],
  publishers: List[Publisher],
  released: Option[String],
  slug: String,
  stores: List[Store],
  suggestions_count: BigDecimal,
  tags: List[Tag],
  tba: Boolean,
  updated: String
)

object GameDetails {
  implicit val format: Format[GameDetails] = Json.using[WithDefaultValues].format
}

case class ParentInfo(
  additions_count: BigDecimal,
  game_series_count: BigDecimal,
  parent_achievements_count: BigDecimal,
  parent_platforms: List[ParentPlatform],
  parents_count: BigDecimal,
)

object ParentInfo {
  implicit val format: Format[ParentInfo] = Json.using[WithDefaultValues].format
}

case class MediaInfo(
  background_image_additional: Option[String],
  background_image: Option[String],
  clip: Option[Clip],
  movies_count: BigDecimal,
  dominant_color: String,
  saturated_color: String,
  screenshots_count: BigDecimal,
  website: String
)

object MediaInfo {
  implicit val format: Format[MediaInfo] = Json.using[WithDefaultValues].format
}

case class CommunityInfo(
  community_rating: Option[BigDecimal],
  metacritic_url: String,
  metacritic: Option[BigDecimal],
  playtime: BigDecimal,
  rating_top: BigDecimal,
  rating: BigDecimal,
  ratings_count: BigDecimal,
  ratings: List[Rating],
  reactions: Option[Reactions],
  reddit_count: BigDecimal,
  reddit_description: String,
  reddit_logo: String,
  reddit_name: String,
  reddit_url: String,
  reviews_count: BigDecimal,
  reviews_text_count: BigDecimal,
  twitch_count: BigDecimal,
  youtube_count: BigDecimal
)

object CommunityInfo {
  implicit val format: Format[CommunityInfo] = Json.using[WithDefaultValues].format
}


// generated

case class AddedByStatus(toplay: Option[BigDecimal], dropped: Option[BigDecimal], owned: Option[BigDecimal], beaten: Option[BigDecimal], yet: Option[BigDecimal], playing: Option[BigDecimal])

object AddedByStatus {
  implicit val format: Format[AddedByStatus] = Json.using[WithDefaultValues].format
}

case class ParentPlatform(platform: ParentPlatformDetails)

object ParentPlatform {
  implicit val format: Format[ParentPlatform] = Json.using[WithDefaultValues].format
}

case class ParentPlatformDetails(id: BigDecimal, name: String, slug: String)

object ParentPlatformDetails {
  implicit val format: Format[ParentPlatformDetails] = Json.using[WithDefaultValues].format
}

case class Reactions(reactions: Map[Int, BigDecimal])

object Reactions {
  private val _reads: Reads[Reactions] = {
    case o: JsObject =>
      try {
        JsSuccess(Reactions(o.value.map {
          case (key, JsNumber(value)) => key.toInt -> value
        }(scala.collection.breakOut)))
      } catch {
        case t: Throwable =>
          JsError(s"Cannot parse $o as Reactions")
      }
  }

  private val _writes: Writes[Reactions] = o =>
    JsObject(o.reactions.map {
      case (key, value) => key.toString -> JsNumber(value)
    })

  implicit val format: Format[Reactions] = new Format[Reactions] {
    override def reads(json: JsValue): JsResult[Reactions] = _reads reads json

    override def writes(o: Reactions): JsValue = _writes writes o
  }
}

case class Tag(name: String, slug: String, id: BigDecimal, games_count: BigDecimal, language: String, image_background: String)

object Tag {
  implicit val format: Format[Tag] = Json.using[WithDefaultValues].format
}

case class Store(id: BigDecimal, url: String, store: StoreDetails)

object Store {
  implicit val format: Format[Store] = Json.using[WithDefaultValues].format
}

case class StoreDetails(name: String, domain: String, slug: String, id: BigDecimal, games_count: BigDecimal, image_background: String)

object StoreDetails {
  implicit val format: Format[StoreDetails] = Json.using[WithDefaultValues].format
}

case class Platform(platform: PlatformDetails, released_at: Option[String], requirements: Option[Requirements])

object Platform {
  implicit val format: Format[Platform] = Json.using[WithDefaultValues].format
}

case class PlatformDetails(name: String, slug: String, id: BigDecimal, games_count: BigDecimal, image_background: String)

object PlatformDetails {
  implicit val format: Format[PlatformDetails] = Json.using[WithDefaultValues].format
}

case class Requirements(minimum: Option[String], recommended: Option[String])

object Requirements {
  implicit val format: Format[Requirements] = Json.using[WithDefaultValues].format
}

case class Genres(name: String, slug: String, id: BigDecimal, games_count: BigDecimal, image_background: String)

object Genres {
  implicit val format: Format[Genres] = Json.using[WithDefaultValues].format
}

case class EsrbRating(id: BigDecimal, name: String, slug: String)

object EsrbRating {
  implicit val format: Format[EsrbRating] = Json.using[WithDefaultValues].format
}

case class Rating(id: BigDecimal, title: String, count: BigDecimal, percent: BigDecimal)

object Rating {
  implicit val format: Format[Rating] = Json.using[WithDefaultValues].format
}

case class Developer(name: String, slug: String, id: BigDecimal, games_count: BigDecimal, image_background: String)

object Developer {
  implicit val format: Format[Developer] = Json.using[WithDefaultValues].format
}

case class Publisher(name: String, slug: String, id: BigDecimal, games_count: BigDecimal, image_background: String)

object Publisher {
  implicit val format: Format[Publisher] = Json.using[WithDefaultValues].format
}

case class Clip(clip: String, clips: Clips, video: String, preview: String)

object Clip {
  implicit val format: Format[Clip] = Json.using[WithDefaultValues].format
}

case class Clips(_320: String, _640: String, full: String)

object Clips {

  import play.api.libs.functional.syntax._

  implicit val format: Format[Clips] = new Format[Clips] {
    override def reads(json: JsValue): JsResult[Clips] = _reads reads json

    override def writes(o: Clips): JsValue = _writes writes o
  }

  private val _reads: Reads[Clips] = (
    (JsPath \ "320").read[String] and
      (JsPath \ "640").read[String] and
      (JsPath \ "full").read[String]
    ) (Clips.apply _)

  private val _writes: Writes[Clips] = o => Json.obj(
    "320" -> o._320,
    "640" -> o._640,
    "full" -> o.full
  )
}