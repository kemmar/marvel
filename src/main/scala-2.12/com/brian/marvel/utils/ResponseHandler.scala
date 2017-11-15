package com.brian.marvel.utils

import akka.http.scaladsl.model.HttpResponse
import akka.http.scaladsl.unmarshalling.{Unmarshal, Unmarshaller}
import akka.stream.Materializer
import com.brian.marvel.domain.ErrorObj
import de.heikoseeberger.akkahttpplayjson.PlayJsonSupport

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

trait ResponseHandler extends PlayJsonSupport {

  implicit class unmarshalResponse(resp: Future[HttpResponse]) {
    def as[T](implicit um: Unmarshaller[HttpResponse, T], mat: Materializer): Future[Either[ErrorObj, T]] =
      resp.flatMap {
        case r: HttpResponse if r.status.isSuccess() => Unmarshal(r).to[T].map(Right(_))
        case r: HttpResponse => Unmarshal(r).to[ErrorObj].map(Left(_))
      }
  }

}
