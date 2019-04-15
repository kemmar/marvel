package com.brian.marvel.controller

import akka.actor.ActorSystem
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import com.brian.marvel.utils.Controller
import io.swagger.annotations._
import javax.ws.rs.Path

@Api(value = "/status", produces = "application/json")
@Path("/status")
class StatusController(statusFSM: (String, String) => Unit)(implicit as: ActorSystem) extends Controller {

  lazy val route: Route = (pathPrefix("status" / Segment) & put) { status =>
    complete {

      (StatusCodes.Accepted, {
        statusFSM("current", status)
        ""
      })
    }
  }
}
