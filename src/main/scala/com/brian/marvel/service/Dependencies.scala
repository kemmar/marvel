package com.brian.marvel.service

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import com.brian.marvel.controller.{CharacterController, WuxiaController}
import com.brian.marvel.endpoints.{GetCharacterEndpoint, GetCharactersEndpoint, GetPowersEndpoint}
import com.brian.marvel.swagger.Swagger
import com.brian.marvel.utils.ErrorHandlerTrait
import com.github.swagger.akka.SwaggerSite
import com.typesafe.config.ConfigFactory
import scala.concurrent.ExecutionContext.Implicits.global

trait Dependencies extends ErrorHandlerTrait with SwaggerSite with Cache {

  implicit val config = ConfigFactory.load()
  implicit val system: ActorSystem
  implicit val materializer: ActorMaterializer

  lazy val getCharactersEndpoint = new GetCharactersEndpoint()
  lazy val getCharacterEndpoint = new GetCharacterEndpoint()
  lazy val getPowersEndpoint = new GetPowersEndpoint()

  lazy val characterController = new CharacterController(getCharactersEndpoint, getCharacterEndpoint, getPowersEndpoint)
  lazy val wuxiaController = new WuxiaController()

  lazy val routes = (handleRejections(myRejectionHandler) & handleExceptions(myExceptionHandler)) {
    characterController.route ~ wuxiaController.route ~ Swagger.routes ~ swaggerSiteRoute
  }

}
