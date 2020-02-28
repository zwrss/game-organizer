package com.github.zwrss.gameorganizer.server.controller

import com.github.zwrss.gameorganizer.server.HttpMethod
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

abstract class Controller {

  type Route = String

  def endpoints: Map[Route, PartialFunction[(HttpMethod, HttpServletRequest, HttpServletResponse), Unit]] = _endpoints

  private var _endpoints: Map[Route, PartialFunction[(HttpMethod, HttpServletRequest, HttpServletResponse), Unit]] = Map.empty

  def endpoint(route: Route)(handle: PartialFunction[(HttpMethod, HttpServletRequest, HttpServletResponse), Unit]): Unit = {
    val effectiveHandle = (_endpoints get route) map (_ orElse handle) getOrElse handle
    _endpoints = _endpoints.updated(route, effectiveHandle)
  }

}

