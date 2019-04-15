package com.brian.marvel.actors

import akka.actor.FSM.Failure
import akka.actor.{Actor, Props}
import com.brian.marvel.StatusRepository
import com.brian.marvel.StatusRepository.StatusChange

import scala.concurrent.ExecutionContext

class ActorB(statusRepository: StatusRepository)(implicit ec: ExecutionContext) extends Actor {
  override def receive: Receive = {
    case a: StatusChange => {
      sender ! statusRepository.updateStatus(a)

      Failure(new Exception("fish"))
    }
  }
}

object ActorB {

  def props(statusRepository: StatusRepository)(implicit ec: ExecutionContext) = Props(new ActorB(statusRepository))

}
