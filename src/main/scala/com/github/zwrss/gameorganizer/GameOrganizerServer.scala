package com.github.zwrss.gameorganizer

import com.github.zwrss.gameorganizer.server.Server
import com.github.zwrss.gameorganizer.server.controller.GameOrganizerController
import com.github.zwrss.gameorganizer.server.controller.ResourcesController

import scala.util.Try

object GameOrganizerServer {
  def main(args: Array[String]): Unit = {

    val port = {
      args.headOption orElse sys.env.get("PORT") flatMap (a => Try(a.toInt).toOption) getOrElse 666
    }

    val server = new Server(port = port, controllers = Seq(
      new ResourcesController("/public/*"),
      new GameOrganizerController
    ))

    server.start()

    println(s"Server started on port $port")

  }
}
