package com.brian.marvel.utils

import akka.http.scaladsl.marshalling.{ToEntityMarshaller, ToResponseMarshaller}
import akka.http.scaladsl.model.{StatusCode, StatusCodes}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.StandardRoute
import com.brian.marvel.domain.ServiceError
import com.brian.marvel.utils.ResponseHandler.ResponseType
import de.heikoseeberger.akkahttpplayjson.PlayJsonSupport

import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.{Duration, _}

trait Controller extends PlayJsonSupport{

  def completion[T: ToResponseMarshaller](resp: => ResponseType[T], statusCode: StatusCode = StatusCodes.OK)(implicit mt: ToEntityMarshaller[T]): StandardRoute = {
    val comp = resp.value.map {
      case Right(v) => complete((statusCode, v))
      case Left(serviceError: ServiceError) =>
        complete((StatusCode.int2StatusCode(serviceError.statusCode.getOrElse(422)), serviceError.toStandardError))
      case Left(error) => complete((StatusCodes.UnprocessableEntity, error))
    }

    //todo: extract timeout
    Await.result(comp, Duration(30, SECONDS))
  }
}
