package com.github.zwrss.gameorganizer

import com.github.zwrss.gameorganizer.common.JsonHttpClient
import com.github.zwrss.gameorganizer.common.Properties
import com.github.zwrss.gameorganizer.console.TablePrinter
import com.github.zwrss.gameorganizer.dataprovider.GameDataProvider
import com.github.zwrss.gameorganizer.dataprovider.RawgClient
import com.github.zwrss.gameorganizer.dql.ast.DqlCommand
import com.github.zwrss.gameorganizer.dql.ast.Select
import com.github.zwrss.gameorganizer.dql.parser.SelectParser
import com.github.zwrss.gameorganizer.game.GameFinder
import com.github.zwrss.gameorganizer.listprovider.SteamClient

import scala.io.StdIn

object GameOrganizer {

  private def SteamKey = Properties.getProperty("SteamKey") getOrElse sys.error("No SteamKey provided!")

  private def SteamId = Properties.getProperty("SteamId") getOrElse sys.error("No SteamId provided!")

  def updateGames(): Unit = {
    val gameDataProvider: GameDataProvider = new RawgClient(JsonHttpClient.caching)

    val gameListProviders = Seq(new SteamClient(JsonHttpClient.default, SteamKey, SteamId))

    val gameNames: Seq[String] = gameListProviders.flatMap(_.getGameList).distinct

    val games = gameNames.flatMap { gameName =>
      println(s"Fetching $gameName")
      val game = gameDataProvider.getData(gameName)
      game match {
        case Some(game) =>
        case None =>
          println(s"No info found about $gameName")
      }
      game
    }

    GameFinder.get.clear()
    GameFinder.get.insert(games)
  }

  def main(args: Array[String]): Unit = try {

    var command = ""

    val parser = new SelectParser {
      def parse(s: String): Select = parseAll(Select, s) match {
        case Success(result, _) => result
        case NoSuccess(message, _) => sys.error(s"Cannot parse [$s]: " + message)
      }
    }

    while (command != "exit") {
      command = StdIn.readLine("Insert command:\n")
      if (command == "update") {
        println("Updating games")
        updateGames()
      } else if (command != "exit") {
        val toPrint = try {
          val dqlCommand = DqlCommand.fromDql(command)
          val dqlResult = dqlCommand.execute(GameFinder.get)
          TablePrinter.format(dqlResult.headers, dqlResult.values)
        } catch {
          case t: Throwable =>
            TablePrinter.format(List("error"), List(List(t.getMessage)))
        }
        println(toPrint)
      }
    }

  } catch {
    case t: Throwable =>
      t printStackTrace System.out
  } finally {
    System.exit(0)
  }

}
