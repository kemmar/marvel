package com.brian.marvel.utils

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.{Rejection, RejectionHandler}
import com.brian.marvel.domain.ErrorObj
import de.heikoseeberger.akkahttpplayjson.PlayJsonSupport

trait RejectionHandlerTrait extends PlayJsonSupport {
  // todo: add more rejection handling
  implicit def myRejectionHandler =
    RejectionHandler.newBuilder()
      .handleAll[Rejection] { _ =>
      complete((StatusCodes.UnprocessableEntity, ErrorObj("", "unhandled rejection")))
    }.handleNotFound {
        extract(_.request.uri.path) { path =>
          complete((StatusCodes.NotFound, ErrorObj("path.not.found", s"path not found: ${path}")))
        }
      }
      .result()
}
