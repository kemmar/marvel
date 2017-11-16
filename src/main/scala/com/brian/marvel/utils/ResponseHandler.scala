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
import cats.data.EitherT

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

    def as[T](implicit um: Unmarshaller[HttpResponse, T], mat: Materializer): ResponseType[T] = resp.flatMap { res =>
        for {
         value <- Unmarshal(res).to[T]
        } yield Right(value)
    }
  }

  import com.brian.marvel.utils.ResponseHandler._
  private def defaultErrorHandler[T](implicit um: Unmarshaller[HttpResponse, T], mat: Materializer): PartialFunction[HttpResponse, ResponseType[T]] = errorHandler orElse {
    case resp: HttpResponse => resp.status match {
      case ClientError(_) => Unmarshal(resp).to[ErrorObj].map(err => Left(ServiceError(err.code, err.message, resp.status.intValue())))
      case _ => Left(ErrorConstants.ServiceError)
    }
  }

  protected def errorHandler[T](implicit um: Unmarshaller[HttpResponse, T], mat: Materializer): PartialFunction[HttpResponse, ResponseType[T]] = PartialFunction.empty[HttpResponse, ResponseType[T]]
}

object ResponseHandler {
  type ResponseType[A] = EitherT[Future, ErrorBase, A]

  implicit def fromFuture[A](fa: Future[Either[ErrorBase, A]]): ResponseType[A] = EitherT(fa)

  implicit def fromEither[A](xor: Either[ErrorBase, A]): ResponseType[A] = fromFuture(Future.successful(xor))

  implicit def fromA[A](a: A): EitherT[Future, ErrorBase, A] = fromEither(Right(a))

}
