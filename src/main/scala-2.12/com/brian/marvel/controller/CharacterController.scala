package com.brian.marvel.controller

import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.Directives._
import com.brian.marvel.endpoints.GetCharactersEndpoint
import de.heikoseeberger.akkahttpplayjson.PlayJsonSupport

class CharacterController(getCharactersEndpoint: GetCharactersEndpoint) extends PlayJsonSupport {

  lazy val route: Route = pathPrefix("characters") {
    pathEnd {
      complete(getCharactersEndpoint.getCharacters)
    }
  }
}
