package com.github.zwrss.gameorganizer.server.controller

import com.github.zwrss.gameorganizer.dql.ast.DqlCommand
import com.github.zwrss.gameorganizer.game.GameFinder
import com.github.zwrss.gameorganizer.server.HttpMethod
import play.api.libs.json.Json

import scala.io.Source


class GameOrganizerController extends Controller {

  endpoint("") {
    case (HttpMethod.GET, _, response) =>
      val resource = Source fromURL getClass.getClassLoader.getResource("index.html")
      val html = resource.getLines().mkString("\n").replaceAllLiterally("$FIELDS$", GameFinder.get.fields.map(_.name).mkString(", "))
      response setContentType "text/html"
      response setStatus 200
      response.getWriter.println(html)
  }

  endpoint("/data") {
    case (HttpMethod.GET, request, response) =>
      val command = request getParameter "command"
      response setContentType "text/html"
      response setStatus 200
      response.getWriter println {
        try {
          val dqlCommand = DqlCommand.fromDql(command)
          val dqlResult = dqlCommand.execute(GameFinder.get)
          Json.obj(
            "headers" -> dqlResult.headers,
            "values" -> dqlResult.values
          )
        } catch {
          case t: Throwable =>
            Json.obj(
              "headers" -> List("error"),
              "values" -> List(List(t.getMessage))
            )
        }
      }
  }

}