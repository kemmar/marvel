package com.brian.marvel.service

import akka.actor.ActorSystem
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer
import com.brian.marvel.controller.CharacterController
import com.brian.marvel.endpoints.{GetCharacterEndpoint, GetCharactersEndpoint}
import com.brian.marvel.utils.RejectionHandlerTrait
import com.typesafe.config.ConfigFactory

trait Dependencies extends RejectionHandlerTrait {

  implicit val config = ConfigFactory.load()
  implicit val system: ActorSystem
  implicit val materializer: ActorMaterializer

  lazy val getCharactersEndpoint = new GetCharactersEndpoint()
  lazy val getCharacterEndpoint = new GetCharacterEndpoint()

  lazy val characterController = new CharacterController(getCharactersEndpoint, getCharacterEndpoint)


  lazy val routes = handleRejections(myRejectionHandler) {
    characterController.route
  }
}
