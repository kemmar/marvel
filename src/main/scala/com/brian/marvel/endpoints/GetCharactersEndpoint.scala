package com.brian.marvel.endpoints

import akka.actor.ActorSystem
import akka.http.scaladsl.model.{HttpRequest, Uri}
import akka.stream.Materializer
import cats.implicits._
import com.brian.marvel.domain.{CharacterResponse, ServiceError}
import com.brian.marvel.service.Cache
import com.brian.marvel.utils.ResponseHandler._
import com.brian.marvel.utils.{Authenticator, ResponseHandler}
import com.google.common.cache.{CacheBuilder, CacheLoader}
import com.typesafe.config.Config

import scala.concurrent.ExecutionContext.Implicits.global

class GetCharactersEndpoint(implicit as: ActorSystem, mat: Materializer, conf: Config) extends ResponseHandler with Cache {

  private lazy val path = "/v1/public/characters"
  private lazy val url: Uri = conf.getString("marvel.url") + path
  private lazy val (stamp, hash) = Authenticator.makeHash("marvel")
  private lazy val limit = 100
  private lazy val apiKey = conf.getString("marvel.apiKey")

  lazy val myCache = modelCache.build(
    new CacheLoader[HttpRequest, ResponseType[CharacterResponse]] {
      def load(request: HttpRequest) = {
        sendLoggedRequest(request).as[CharacterResponse, ServiceError]
      }
    }
  )

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

          myCache.get(req(start))
        }

      seqFutures.reduce { (f, a) =>
        for {
          x <- f
          y <- a
        } yield y.copy(characters = y.characters ++ x.characters)
      }
    }

    for {
      cr <- makeCall
      bCr <- makeBatchCall(cr)
    } yield bCr.characters.map(_.id)
  }

}
