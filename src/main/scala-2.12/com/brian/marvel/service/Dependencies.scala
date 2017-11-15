package com.brian.marvel.service

import com.brian.marvel.controller.CharacterController

trait Dependencies {
  lazy val characterController = new CharacterController()


  val routes = characterController.route
}
