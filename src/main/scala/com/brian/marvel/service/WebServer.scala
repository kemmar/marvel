package com.brian.marvel.service

import akka.actor.ActorSystem
import akka.http.scaladsl.server.HttpApp
import akka.stream.ActorMaterializer
import com.brian.marvel.utils.DBConfiguration

class WebServer extends HttpApp with Dependencies {
  implicit val system = ActorSystem()
  implicit val materializer = ActorMaterializer()

  lazy val dbConfig = new DBConfiguration()
}
