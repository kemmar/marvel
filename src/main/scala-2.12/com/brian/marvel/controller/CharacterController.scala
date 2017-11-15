package com.brian.marvel.controller

import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.Directives._

class CharacterController {

  lazy val route: Route = pathPrefix("characters") {
    complete(???)
  }

}
