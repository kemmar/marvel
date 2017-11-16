package com.brian.marvel.endpoints

import akka.actor.ActorSystem
import akka.http.scaladsl.model.{HttpRequest, Uri}
import akka.stream.Materializer
import cats.implicits._
import com.brian.marvel.domain.{CharacterResponse, MarvelCharacter}
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

    def buildCharacterList(offset: Int = 0, total: Option[Int] = None): ResponseType[CharacterResponse] = {
      val req =
        HttpRequest(uri =
          url.withRawQueryString(s"limit=$limit&offset=$offset&apikey=$apiKey&hash=$hash&ts=$stamp"))

      def task(lastCall: CharacterResponse): ResponseType[CharacterResponse] = for {
        next <- buildCharacterList(offset + limit, Some(lastCall.total))
      } yield next.copy(characters = next.characters ++ lastCall.characters)

      def makeCall: ResponseType[CharacterResponse] = {
        for {
          response <- sendLoggedRequest(req).as[CharacterResponse]
          fin <- task(response)
        } yield fin
      }

      total match {
        case Some(total) => {
          if ((offset + limit) <= total) {
            makeCall
          }
          else Right(CharacterResponse(offset + limit, total, Seq.empty[MarvelCharacter]))
        }
        case None => {
          makeCall
        }
      }
    }

    for {
      chr <- buildCharacterList()
    } yield chr.characters.map(_.id)
  }
}
