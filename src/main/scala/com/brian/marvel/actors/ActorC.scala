package com.brian.marvel.actors

import akka.actor.{Actor, Props}
import com.brian.marvel.StatusRepository
import com.brian.marvel.StatusRepository.StatusChange
import slick.jdbc.MySQLProfile.api._

import scala.concurrent.ExecutionContext

class ActorC(statusRepository: StatusRepository)(implicit ec: ExecutionContext) extends Actor {

  def receive: Receive = {
    case a: StatusChange => sender ! statusRepository.updateStatus(a)
    case complete: DBIOAction[_, _, _] => statusRepository.executeUpdate(complete)
  }

}

object ActorC {

  def props(statusRepository: StatusRepository)(implicit ec: ExecutionContext) = Props(new ActorC(statusRepository))

}
