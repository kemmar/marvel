package com.brian.marvel.endpoints

import akka.actor.ActorSystem
import akka.http.scaladsl.model.{HttpRequest, Uri}
import akka.stream.Materializer
import cats.implicits._
import com.brian.marvel.domain.{CharacterResponse, ServiceError}
import com.brian.marvel.utils.ResponseHandler._
import com.brian.marvel.utils.{Authenticator, ResponseHandler}
import com.typesafe.config.Config

import scala.concurrent.ExecutionContext.Implicits.global

class GetCharactersEndpoint(implicit as: ActorSystem, mat: Materializer, conf: Config) extends ResponseHandler {

  lazy val path = "/v1/public/characters"
  lazy val url: Uri = conf.getString("marvel.url") + path
  lazy val (stamp, hash) = Authenticator.makeHash("marvel")

  lazy val limit = 100
  lazy val apiKey = conf.getString("marvel.apiKey")

  def getCharacters: ResponseType[Seq[Int]] = {

    def numToChunks(position: Int, chunk: Int, total: Int): Seq[Int] = {
      val chunked = position + chunk

      if (chunked < total) position +: numToChunks(chunked, chunk, total)
      else Seq(position)
    }

    def req(offset: Int) =
      HttpRequest(uri =
        url.withRawQueryString(s"limit=$limit&offset=$offset&apikey=$apiKey&hash=$hash&ts=$stamp"))

    def makeCall: ResponseType[CharacterResponse] = sendLoggedRequest(req(0)).as[CharacterResponse, ServiceError]

    def makeBatchCall(cr: CharacterResponse): ResponseType[CharacterResponse] = {

      val seqFutures: Seq[ResponseType[CharacterResponse]] =
        numToChunks(limit, limit, cr.total).map { start =>
          sendLoggedRequest(req(start)).as[CharacterResponse, ServiceError]
        }

      seqFutures.reduce { (f, a) =>
        for {
          x <- f
          y <- a
        } yield x.copy(characters = x.characters ++ y.characters)
      }
    }

    for {
      cr <- makeCall
      bCr <- makeBatchCall(cr)
    } yield bCr.characters.map(_.id)
  }

}
