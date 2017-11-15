package com.brian.marvel.service

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import com.brian.marvel.controller.CharacterController
import com.brian.marvel.endpoints.GetCharactersEndpoint

trait Dependencies {

  implicit val system = ActorSystem()
  implicit val materializer = ActorMaterializer()

  lazy val getCharactersEndpoint = new GetCharactersEndpoint()
  lazy val characterController = new CharacterController(getCharactersEndpoint)


  val routes = characterController.route
}
