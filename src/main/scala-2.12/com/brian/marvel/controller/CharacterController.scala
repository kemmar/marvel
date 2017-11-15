package com.brian.marvel.controller

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import com.brian.marvel.endpoints.GetCharactersEndpoint
import com.brian.marvel.utils.Controller

class CharacterController(getCharactersEndpoint: GetCharactersEndpoint) extends Controller {

  lazy val route: Route = pathPrefix("characters") {
    pathEnd {
      completion(getCharactersEndpoint.getCharacters)
    }
  }
}
