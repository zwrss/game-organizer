package com.github.zwrss.gameorganizer.listprovider

trait GameListProvider {

  def getGameList: Seq[String]

}
