package com.brian.marvel.service

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import com.brian.marvel.controller.CharacterController
import com.brian.marvel.endpoints.GetCharactersEndpoint
import com.typesafe.config.{Config, ConfigFactory}

trait Dependencies {

  implicit val config = ConfigFactory.load()
  implicit val system: ActorSystem
  implicit val materializer: ActorMaterializer

  lazy val getCharactersEndpoint = new GetCharactersEndpoint()
  lazy val characterController = new CharacterController(getCharactersEndpoint)


  lazy val routes = characterController.route
}
