package com.brian.marvel.fsm

import java.util.UUID

import akka.actor.{ActorRef, ActorSystem, FSM, Props}
import com.brian.marvel.StatusRepository.StatusChange
import com.brian.marvel.fsm.SiteStatusChangingFSM._
import slick.dbio.{Effect, NoStream}
import slick.jdbc.MySQLProfile.api._
import slick.sql.FixedSqlAction


object SiteStatusChangingFSM {

  sealed trait State

  case object Idle extends State

  case object WaitingForA extends State

  case object WaitingForB extends State

  case object WaitingForC extends State

  sealed trait Data

  case object Uninitialized extends Data

  case class TransitionalData[T, S <: NoStream, E <: Effect](data: DBIOAction[T, S, E]) extends Data

  def onRequestFsm(
                    currentStatus: String,
                    status: String,
                    actorA: ActorRef,
                    actorB: ActorRef,
                    actorC: ActorRef)(implicit as: ActorSystem) = {
    val statusUUID = UUID.randomUUID().toString
    as.actorOf(
      Props(
        new SiteStatusChangingFSM(
          statusUUID,
          currentStatus,
          status,
          actorA: ActorRef,
          actorB: ActorRef,
          actorC: ActorRef)), s"actor-fsm-$statusUUID")
  }

}

class SiteStatusChangingFSM(status_id: String,
                            status: String,
                            currentStatus: String,
                            actorA: ActorRef,
                            actorB: ActorRef,
                            actorC: ActorRef)
  extends FSM[State, Data] {

  import scala.concurrent.duration._
  import scala.concurrent.ExecutionContext.Implicits.global

  val bool: String => String = str => if (status == "status-total") status_id else status_id + str

  startWith(Idle, Uninitialized, Some(5 seconds))

  self ! (currentStatus, status)

  when(Idle) {
    case Event((_, "on_hold"), Uninitialized) => {
      actorA ! StatusChange(bool("A"), s"$status - A")

      goto(WaitingForA)
    }

    case Event(("on_hold", _), Uninitialized) => {
      println(s"$currentStatus, $status")
      stop()
    }
  }

  when(WaitingForA) {
    case Event(sql: FixedSqlAction[_, _, _], _) => {
      actorB ! StatusChange(bool("B"), s"$status - B")


      goto(WaitingForB).using(TransitionalData(sql))
    }
  }

  when(WaitingForB) {
    case Event(sql: FixedSqlAction[_, _, _], TransitionalData(t)) => {
      actorC ! StatusChange(bool("C"), s"$status - C")

      goto(WaitingForC).using(
        TransitionalData(t andThen sql)
      )
    }
  }

  when(WaitingForC) {
    case Event(sql: FixedSqlAction[_, _, _], TransitionalData(t)) => {

      actorC ! (t andThen sql)

      stop()
    }
  }
}
