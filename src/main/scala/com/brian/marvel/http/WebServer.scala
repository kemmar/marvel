package com.brian.marvel.http

import akka.actor.ActorSystem
import akka.http.scaladsl.server.HttpApp
import akka.stream.SystemMaterializer

class WebServer extends HttpApp with Dependencies {
  implicit val system = ActorSystem()
  implicit val materializer = SystemMaterializer(system)
}
