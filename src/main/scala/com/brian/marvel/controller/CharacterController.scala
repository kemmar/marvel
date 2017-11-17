package com.brian.marvel.controller

import cats.implicits._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import com.brian.marvel.endpoints.{GetCharacterEndpoint, GetCharactersEndpoint, GetPowersEndpoint}
import com.brian.marvel.utils.Controller
import com.brian.marvel.utils.ResponseHandler._

import scala.concurrent.ExecutionContext.Implicits.global

class CharacterController(getCharactersEndpoint: GetCharactersEndpoint, getCharacterEndpoint: GetCharacterEndpoint, getPowersEndpoint: GetPowersEndpoint) extends Controller {

  lazy val powers = (path(Segment / "powers") & parameter('language ? "en")) { (characterId, language) =>
    completion {
      for {
        character <- getCharacterEndpoint.getCharacter(characterId)
        power <- getPowersEndpoint.getPowersTranslated(character, language)
      } yield power
    }
  }

  lazy val route: Route = pathPrefix("characters") {
    powers ~
    path(Segment) { characterId =>
        completion(for(character <- getCharacterEndpoint.getCharacter(characterId)) yield character.copy(wiki = None))
    } ~
      pathEnd {
      completion(getCharactersEndpoint.getCharacters)
    }
  }
}
