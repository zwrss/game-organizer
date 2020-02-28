package com.github.zwrss.gameorganizer.common

import scala.concurrent.Await
import scala.concurrent.ExecutionContext
import scala.concurrent.Future
import scala.concurrent.duration.Duration

trait Cache[K, V] {

  def loadF(key: K)(loader: => Future[V]): Future[V]

  def load(key: K)(loader: => V): V

  def clear(): Unit

  def clear(key: K): Unit

}

trait SyncCache[K, V] extends Cache[K, V] {

  final override def loadF(key: K)(loader: => Future[V]): Future[V] = Future {
    load(key)(Await.result(loader, Duration.Inf))
  }(ExecutionContext.global)

}

trait AsyncCache[K, V] extends Cache[K, V] {

  final override def load(key: K)(loader: => V): V = {
    Await.result(loadF(key)(Future(loader)(ExecutionContext.global)), Duration.Inf)
  }

}
