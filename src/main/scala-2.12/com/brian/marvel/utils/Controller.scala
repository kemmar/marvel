package com.brian.marvel.utils

import akka.http.scaladsl.marshalling.ToResponseMarshaller
import akka.http.scaladsl.model.{StatusCode, StatusCodes}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.StandardRoute
import com.brian.marvel.domain.ServiceError
import com.brian.marvel.utils.ResponseHandler.ResponseType
import de.heikoseeberger.akkahttpplayjson.PlayJsonSupport

import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.{Duration, _}

trait Controller extends PlayJsonSupport {

  def completion[T: ToResponseMarshaller](resp: => ResponseType[T], statusCode: StatusCode = StatusCodes.OK): StandardRoute = {
    val comp = resp.map {
        case Right(v) => complete(v)
        case Left(serviceError: ServiceError) =>
          complete((StatusCode.int2StatusCode(serviceError.statusCode), serviceError.toStandardError))
        case Left(error) => complete(error)
    }

    Await.result(comp , Duration(10, SECONDS))
  }

}
