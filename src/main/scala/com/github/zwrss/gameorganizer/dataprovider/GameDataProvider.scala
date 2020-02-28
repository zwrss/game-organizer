package com.github.zwrss.gameorganizer.dataprovider

import com.github.zwrss.gameorganizer.game.Game

trait GameDataProvider {

  def getData(gameName: String): Option[Game]

}
