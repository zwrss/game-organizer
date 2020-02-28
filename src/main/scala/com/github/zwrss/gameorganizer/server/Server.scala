package com.github.zwrss.gameorganizer.server

import com.github.zwrss.gameorganizer.server.controller.Controller
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import org.eclipse.jetty.server.session.SessionHandler
import org.eclipse.jetty.server.{Server => JettyServer}
import org.eclipse.jetty.servlet.DefaultServlet
import org.eclipse.jetty.servlet.ServletContextHandler
import org.eclipse.jetty.servlet.ServletHolder

class Server(controllers: Seq[Controller], port: Int) {

  private val handle404: PartialFunction[(HttpMethod, HttpServletRequest, HttpServletResponse), Unit] = {
    case (_, _, response) => response sendError 404
  }

  private def getServlets: Seq[(ServletHolder, String)] = {
    var _endpoints = Map.empty[String, PartialFunction[(HttpMethod, HttpServletRequest, HttpServletResponse), Unit]]
    controllers.foreach { controller =>
      controller.endpoints.foreach {
        case (route, handle) =>
          val effectiveHandle = (_endpoints get route) map (_ orElse handle) getOrElse handle
          _endpoints = _endpoints.updated(route, effectiveHandle)
      }
    }

    _endpoints = _endpoints.updated("/*", handle404)

    _endpoints.toSeq.map {
      case (route, handle) =>
        val servlet = new DefaultServlet {
          override def doGet(request: HttpServletRequest, response: HttpServletResponse): Unit = {
            (handle orElse handle404) (HttpMethod.GET, request, response)
          }

          override def doPost(request: HttpServletRequest, response: HttpServletResponse): Unit = {
            (handle orElse handle404) (HttpMethod.POST, request, response)
          }
        }
        new ServletHolder(servlet) -> route
    }
  }

  private lazy val _server: JettyServer = {
    val server = new JettyServer(port)
    val handler = new ServletContextHandler(ServletContextHandler.SESSIONS)
    handler setContextPath "/"
    server setHandler handler
    getServlets.foreach {
      case (servlet, route) => handler.addServlet(servlet, route)
    }
    val sessionHandler = new SessionHandler
    sessionHandler setUsingCookies true
    handler setSessionHandler sessionHandler
    server
  }

  def start(): Unit = _server.start()

  def stop(): Unit = _server.stop()

}
