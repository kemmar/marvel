package com.brian.marvel.utils

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.StatusCodes.ClientError
import akka.http.scaladsl.model.{HttpRequest, HttpResponse}
import akka.http.scaladsl.unmarshalling.{Unmarshal, Unmarshaller}
import akka.stream.Materializer
import com.brian.marvel.domain.{ErrorBase, ErrorObj, ServiceError}
import com.brian.marvel.service.ErrorConstants
import com.brian.marvel.utils.ResponseHandler._
import de.heikoseeberger.akkahttpplayjson.PlayJsonSupport

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

trait ResponseHandler extends PlayJsonSupport {

  def sendLoggedRequest(req: HttpRequest)(implicit as: ActorSystem, mat: Materializer): Future[HttpResponse] = {
    println(req)
    Http().singleRequest(req).map { resp =>
      println(resp)
      resp
    }
  }

  implicit class UnmarshalResponse(resp: Future[HttpResponse]) {
    def as[T](implicit um: Unmarshaller[HttpResponse, T], mat: Materializer): ResponseType[T] = {
      val res = resp.flatMap {
        case r: HttpResponse if r.status.isSuccess() => {
          println(r.entity)
          Unmarshal(r).to[T].map(Right(_))
        }
        case r: HttpResponse => {
          println(r.entity)
          defaultErrorHandler[T].apply(r)
        }
      }

      res.failed map {
        case _: Throwable => Left(ErrorConstants.ServiceError)
      }

      res
    }
  }

  private def defaultErrorHandler[T](implicit um: Unmarshaller[HttpResponse, T], mat: Materializer): PartialFunction[HttpResponse, ResponseType[T]] = errorHandler orElse {
    case resp: HttpResponse => resp.status match {
      case ClientError(_) => Unmarshal(resp).to[ErrorObj].map(err => Left(ServiceError(err.code, err.message, resp.status.intValue())))
      case _ => Left(ErrorConstants.ServiceError)
    }
  }

  protected def errorHandler[T](implicit um: Unmarshaller[HttpResponse, T], mat: Materializer): PartialFunction[HttpResponse, ResponseType[T]] = PartialFunction.empty[HttpResponse, ResponseType[T]]
}

object ResponseHandler {
  type ResponseType[T] = Future[Either[ErrorBase, T]]

  implicit def eitherToResponseType[T](value: Either[ErrorBase, T]): ResponseType[T] = Future { value }

}
