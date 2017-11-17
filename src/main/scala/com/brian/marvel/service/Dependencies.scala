package com.brian.marvel.service

import akka.actor.ActorSystem
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer
import com.brian.marvel.controller.CharacterController
import com.brian.marvel.endpoints.{GetCharacterEndpoint, GetCharactersEndpoint, GetPowersEndpoint}
import com.brian.marvel.utils.ErrorHandlerTrait
import com.typesafe.config.ConfigFactory

trait Dependencies extends ErrorHandlerTrait {

  implicit val config = ConfigFactory.load()
  implicit val system: ActorSystem
  implicit val materializer: ActorMaterializer

  lazy val getCharactersEndpoint = new GetCharactersEndpoint()
  lazy val getCharacterEndpoint = new GetCharacterEndpoint()
  lazy val getPowersEndpoint = new GetPowersEndpoint()

  lazy val characterController = new CharacterController(getCharactersEndpoint, getCharacterEndpoint, getPowersEndpoint)

  lazy val routes = (handleRejections(myRejectionHandler) & handleExceptions(myExceptionHandler)) {
    characterController.route
  }
}
