package com.github.zwrss.gameorganizer.common

import java.io.File

import io.netty.handler.codec.http.cookie.Cookie
import org.asynchttpclient.AsyncCompletionHandler
import org.asynchttpclient.AsyncHttpClient
import org.asynchttpclient.DefaultAsyncHttpClient
import org.asynchttpclient.Response
import play.api.libs.json.JsValue
import play.api.libs.json.Json

import scala.concurrent.Await
import scala.concurrent.Future
import scala.concurrent.Promise
import scala.concurrent.duration.Duration

class JsonHttpClient(client: AsyncHttpClient) {

  def close(): Unit = client.close()

  protected def getF(url: String, queryParameters: Map[String, List[String]] = Map.empty, cookies: Seq[Cookie] = Seq.empty): Future[JsValue] = {
    val promise = Promise.apply[JsValue]()
    val request = client.prepareGet(url)
    queryParameters foreach {
      case (key, values) => values.foreach { value =>
        request.addQueryParam(key, value)
      }
    }
    cookies foreach { cookie =>
      request.addCookie(cookie)
    }
    request.execute(new AsyncCompletionHandler[Response] {

      override def onCompleted(response: Response): Response = {
        promise success Json.parse(response.getResponseBody)
        response
      }

      override def onThrowable(t: Throwable): Unit = {
        promise failure t
      }

    })
    promise.future
  }

  def get(url: String, queryParameters: Map[String, List[String]] = Map.empty, cookies: Seq[Cookie] = Seq.empty): JsValue = {
    Await.result(getF(url, queryParameters, cookies), Duration.Inf)
  }

}

class CachingJsonHttpClient(client: AsyncHttpClient) extends JsonHttpClient(client) {

  private val cache = new FileCache[(String, Map[String, List[String]]), JsValue](new File("cache", "jsonhttp"), Option(".json")) {
    override def valueToString(v: JsValue): String = Json prettyPrint v

    override def keyToString(k: (String, Map[String, List[String]])): String = {
      val (url, params) = k
      val urlWithParams = Seq(url) ++ params.map {
        case (key, values) => key + "=" + values.mkString("+")
      }.toSeq
      urlWithParams.mkString("_")
    }

    override def valueFromString(v: String): JsValue = Json parse v
  }

  override protected def getF(url: String, queryParameters: Map[String, List[String]], cookies: Seq[Cookie]): Future[JsValue] = {
    cache.loadF(url -> queryParameters)(super.getF(url, queryParameters, cookies))
  }

}

object JsonHttpClient {
  lazy val default: JsonHttpClient = new JsonHttpClient(new DefaultAsyncHttpClient)
  lazy val caching: JsonHttpClient = new CachingJsonHttpClient(new DefaultAsyncHttpClient)
}