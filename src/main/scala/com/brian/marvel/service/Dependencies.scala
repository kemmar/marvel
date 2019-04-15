package com.brian.marvel.service

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import com.brian.marvel.StatusRepository
import com.brian.marvel.actors.{ActorA, ActorB, ActorC}
import com.brian.marvel.controller.StatusController
import com.brian.marvel.fsm.SiteStatusChangingFSM
import com.brian.marvel.utils.{DBConfiguration, ErrorHandlerTrait}
import com.github.swagger.akka.SwaggerSite
import com.typesafe.config.ConfigFactory

import scala.concurrent.ExecutionContext.Implicits.global

trait Dependencies extends ErrorHandlerTrait with SwaggerSite with Cache {

  implicit val config = ConfigFactory.load()
  implicit val system: ActorSystem
  implicit val materializer: ActorMaterializer

  def dbConfig: DBConfiguration

  lazy val statusRepository = new StatusRepository(dbConfig.database)
  lazy val actorA = system.actorOf(ActorA.props(statusRepository), "actorA")
  lazy val actorB = system.actorOf(ActorB.props(statusRepository), "actorB")
  lazy val actorC = system.actorOf(ActorC.props(statusRepository), "actorC")
  lazy val fsm: (String, String) => Unit = (str, currentState) => SiteStatusChangingFSM.onRequestFsm(currentState, str, actorA, actorB, actorC)

  lazy val characterController = new StatusController(fsm)

  lazy val routes = (handleRejections(myRejectionHandler) & handleExceptions(myExceptionHandler)) {
    characterController.route
  }

}
