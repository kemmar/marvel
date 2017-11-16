package com.brian.marvel.endpoints

import akka.actor.ActorSystem
import akka.http.scaladsl.model.{HttpRequest, Uri}
import akka.stream.Materializer
import com.brian.marvel.domain.{CharacterResponse, ErrorBase, MarvelCharacter}
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

      def task(last: Either[ErrorBase, CharacterResponse]): ResponseType[CharacterResponse] = for {
        next <- buildCharacterList(offset + limit, last.toOption.map(_.total))
      } yield for {
        l <- last
        n <- next
      } yield n.copy(characters = n.characters ++ l.characters)

      def makeCall = {
        sendLoggedRequest(req).as[CharacterResponse].flatMap { response =>
          if (response.isRight) {
            task(response)
          } else response
        }
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

    buildCharacterList().map(_.map(_.characters.map(_.id)))
  }
}
