package com.brian.marvel.utils

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{HttpRequest, HttpResponse}
import akka.http.scaladsl.unmarshalling.{Unmarshal, Unmarshaller}
import akka.stream.Materializer
import cats.data.EitherT
import com.brian.marvel.domain.{ErrorBase, ServiceError}
import com.brian.marvel.service.ErrorConstants
import com.brian.marvel.utils.ResponseHandler._
import de.heikoseeberger.akkahttpplayjson.PlayJsonSupport

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Failure, Success}

trait ResponseHandler extends PlayJsonSupport {

  def sendLoggedRequest(req: HttpRequest)(implicit as: ActorSystem, mat: Materializer): Future[HttpResponse] = {
    println(req)
    Http().singleRequest(req).map { resp =>
      println(resp)
      resp
    }
  }

  implicit class UnmarshalResponse(resp: Future[HttpResponse]) {

    def as[T, E <: ErrorBase](implicit um: Unmarshaller[HttpResponse, T], umErr: Unmarshaller[HttpResponse, E], mat: Materializer): ResponseType[T] = resp.flatMap { res =>

      val f = for {
        value <- Unmarshal(res).to[T]
      } yield Right(value)

      f.onComplete {
        case Success(s) => s
        case Failure(_) => defaultErrorHandler(umErr, mat).apply(res).map(Left(_))
      }

      f
    }
  }

  private def defaultErrorHandler[E <: ErrorBase](implicit umErr: Unmarshaller[HttpResponse, E], mat: Materializer): PartialFunction[HttpResponse, Future[ServiceError]] = errorHandler orElse {
    case resp: HttpResponse => {
      val marshalled = Unmarshal(resp).to[E].map(err => ServiceError(err.code, err.message, resp.status.intValue()))

      marshalled.onComplete {
        case Success(s) => s
        case Failure(_) => ErrorConstants.ServiceErrorConstant
      }
      marshalled
    }
  }

  protected def errorHandler[E <: ErrorBase](implicit umErr: Unmarshaller[HttpResponse, E], mat: Materializer): PartialFunction[HttpResponse, Future[ServiceError]] = PartialFunction.empty[HttpResponse, Future[ServiceError]]
}

object ResponseHandler {
  type ResponseType[A] = EitherT[Future, ErrorBase, A]

  implicit def fromFuture[A](fa: Future[Either[ErrorBase, A]]): ResponseType[A] = EitherT(fa)

  implicit def fromEither[A](either: Either[ErrorBase, A]): ResponseType[A] = fromFuture(Future.successful(either))

  implicit def fromA[A](a: A): EitherT[Future, ErrorBase, A] = fromEither(Right(a))

}
