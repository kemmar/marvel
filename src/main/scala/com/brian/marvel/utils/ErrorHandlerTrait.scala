package com.brian.marvel.utils

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.{ExceptionHandler, Rejection, RejectionHandler, ValidationRejection}
import com.brian.marvel.domain.ErrorObj
import de.heikoseeberger.akkahttpplayjson.PlayJsonSupport

trait ErrorHandlerTrait extends PlayJsonSupport {
  // todo: add more rejection handling
  def myRejectionHandler =
    RejectionHandler.newBuilder()
      .handle({
        case rej: ValidationRejection => complete((StatusCodes.UnprocessableEntity, ErrorObj("validation.rejection", rej.message)))
      })
      .handleAll[Rejection] { r =>
      println(s"Rejection: $r")
      complete((StatusCodes.UnprocessableEntity, ErrorObj("unhandled.rejection", "unhandled rejection")))
    }.handleNotFound {
      extract(_.request.uri.path) { path =>
        complete((StatusCodes.NotFound, ErrorObj("path.not.found", s"path not found: ${path}")))
      }
    }
      .result()

  val myExceptionHandler = ExceptionHandler {
    case e: Throwable =>
      extractUri { uri =>
        println(s"Request to $uri could not be handled normally")
        println(s"Error: ${e.getCause}")
        e.printStackTrace()
        complete((StatusCodes.ServiceUnavailable, ErrorObj("unhandled.error", e.getMessage)))
      }
  }
}
