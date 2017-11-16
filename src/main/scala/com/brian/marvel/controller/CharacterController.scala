package com.brian.marvel.controller

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import com.brian.marvel.endpoints.{GetCharacterEndpoint, GetCharactersEndpoint}
import com.brian.marvel.utils.Controller

import scala.concurrent.ExecutionContext.Implicits.global

class CharacterController(getCharactersEndpoint: GetCharactersEndpoint, getCharacterEndpoint: GetCharacterEndpoint) extends Controller {

  lazy val powers = (path(Segment / "powers") & parameter('language ? "en")) { (characterId, language) =>
    completion {
      for {
        character <- getCharacterEndpoint.getCharacter(characterId)
      } yield character.right.map(_.wikiRedirect(language))
    }
  }

  lazy val route: Route = pathPrefix("characters") {
    powers ~
    path(Segment) { characterId =>
        completion(getCharacterEndpoint.getCharacter(characterId))
    } ~
      pathEnd {
      completion(getCharactersEndpoint.getCharacters)
    }
  }
}
