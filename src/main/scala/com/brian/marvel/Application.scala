package com.brian.marvel

import com.brian.marvel.service.WebServer

object Application extends App {
  val webServer = new WebServer()

  webServer.startServer("localhost", 8080)
}
