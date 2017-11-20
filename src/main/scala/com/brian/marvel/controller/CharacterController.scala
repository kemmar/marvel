package com.brian.marvel.controller

import javax.ws.rs.Path

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import cats.implicits._
import com.brian.marvel.domain.{MarvelCharacter, Powers}
import com.brian.marvel.endpoints.{GetCharacterEndpoint, GetCharactersEndpoint, GetPowersEndpoint}
import com.brian.marvel.utils.Controller
import io.swagger.annotations._

import scala.concurrent.ExecutionContext.Implicits.global

@Api(value = "/characters", produces = "application/json")
@Path("/characters")
class CharacterController(getCharactersEndpoint: GetCharactersEndpoint, getCharacterEndpoint: GetCharacterEndpoint, getPowersEndpoint: GetPowersEndpoint) extends Controller {

  private lazy val powers = (path(Segment / "powers") & parameter('language ? "en")) { (characterId, language) =>
    completion {
      for {
        character <- getCharacterEndpoint.getCharacter(characterId)
        power <- getPowersEndpoint.getPowersTranslated(character, language)
      } yield power
    }
  }

  private lazy val getCharacter = path(Segment) { characterId =>
    completion(for (character <- getCharacterEndpoint.getCharacter(characterId)) yield character.copy(wiki = None))
  }

  private lazy val getCharacters = pathEnd {
    completion(getCharactersEndpoint.getCharacters)
  }

  lazy val route: Route = pathPrefix("characters") {
    powers ~ getCharacter ~ getCharacters
  }
}
