package com.brian.marvel.endpoints

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{HttpRequest, Uri}
import akka.stream.Materializer
import com.brian.marvel.domain.{CharacterResponse, ErrorBase, MarvelCharacter, ServiceError}
import com.brian.marvel.utils.{Authenticator, ResponseHandler}
import com.brian.marvel.utils.ResponseHandler.ResponseType
import com.typesafe.config.Config

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class GetCharactersEndpoint(implicit as: ActorSystem, mat: Materializer, conf: Config) extends ResponseHandler {

  val path = "/v1/public/characters"
  val url: Uri = conf.getString("marvel.url") + path
  val (stamp, hash) = Authenticator.makeHash("marvel")

  val limit = 100
  val apiKey = conf.getString("marvel.apiKey")

  def getCharacters: ResponseType[Seq[Int]] = {

    def buildCharacterList(offset: Int = 0, total: Option[Int] = None): ResponseType[CharacterResponse] = {
      val req =
        HttpRequest(uri =
          url.withRawQueryString(s"limit=$limit&offset=$offset&apikey=$apiKey&hash=$hash&ts=$stamp"))

      def task(last: Either[ErrorBase, CharacterResponse]): Future[Either[ErrorBase, CharacterResponse]] = for {
        next <- buildCharacterList(offset + limit, last.toOption.map(_.total))
      } yield for {
        l <- last
        n <- next
      } yield n.copy(characters = n.characters ++ l.characters)

      def makeCall = {
        sendLoggedRequest(req).as[CharacterResponse].flatMap { response =>
          if (response.isRight) {
            task(response)
          } else Future {
            response
          }
        }
      }

      total match {
        case Some(total) => {
          if ((offset + limit) <= total) {
            makeCall
          }
          else Future {
            Right(CharacterResponse(offset + limit, total, Seq.empty[MarvelCharacter]))
          }
        }
        case None => {
          makeCall
        }
      }
    }

    buildCharacterList().map(_.map(_.characters.map(_.id)))
  }
}
