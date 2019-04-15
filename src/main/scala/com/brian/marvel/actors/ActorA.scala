package com.brian.marvel.actors

import akka.actor.{Actor, Props}
import akka.pattern.PipeToSupport
import com.brian.marvel.StatusRepository
import com.brian.marvel.StatusRepository.StatusChange

import scala.concurrent.ExecutionContext

class ActorA(statusRepository: StatusRepository)(implicit ec: ExecutionContext) extends Actor with PipeToSupport {
  override def receive: Receive = {
    case a: StatusChange => sender ! statusRepository.updateStatus(a)
  }
}

object ActorA {

  def props(statusRepository: StatusRepository)(implicit ec: ExecutionContext) = Props(new ActorA(statusRepository))

}
