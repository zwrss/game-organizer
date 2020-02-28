package com.github.zwrss.gameorganizer.common

import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat

trait Logging { // todo real logging

  private def _log(tag: String)(msg: Any): Unit = {
    val msgStr = msg.toString
    val time = Logging.DateFormat.print(new DateTime)
    msg match {
      case t: Throwable =>
        println(s"$time [$tag] Exception logged: ")
        t printStackTrace System.out
      case _ =>
        println(s"$time [$tag] $msgStr")
    }
  }

  final protected def info(msg: Any): Unit = _log("WARN")(msg)

  final protected def debug(msg: Any): Unit = _log("DEBUG")(msg)

  final protected def warn(msg: Any): Unit = _log("WARN")(msg)

  final protected def error(msg: Any): Unit = _log("ERROR")(msg)

}

object Logging {
  private val DateFormat = DateTimeFormat.forPattern("HH:mm:ss")
}
