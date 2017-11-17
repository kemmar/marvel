package com.brian.marvel.endpoints

import akka.actor.ActorSystem
import akka.http.scaladsl.model.{HttpRequest, Uri}
import akka.stream.Materializer
import com.brian.marvel.domain.{CharacterResponse, MarvelCharacter, ServiceError}
import com.brian.marvel.utils.ResponseHandler._
import com.brian.marvel.utils.{Authenticator, ResponseHandler}
import com.typesafe.config.Config

import scala.concurrent.ExecutionContext.Implicits.global

class GetCharacterEndpoint(implicit as: ActorSystem, mat: Materializer, conf: Config) extends ResponseHandler {

  lazy val (stamp, hash) = Authenticator.makeHash("marvel")

  lazy val apiKey = conf.getString("marvel.apiKey")

  def getCharacter(id: String): ResponseType[MarvelCharacter] = {
    val path = s"/v1/public/characters/$id"
    val url: Uri = conf.getString("marvel.url") + path
    val req = HttpRequest(uri = url.withRawQueryString(s"apikey=$apiKey&hash=$hash&ts=$stamp"))

    sendLoggedRequest(req).as[CharacterResponse, ServiceError].value.map {
      case Right(s) if s.characters.size > 1 =>  Left(ServiceError("popular.character", "too many responses"))
      case Right(s) if s.characters.isEmpty =>  Left(ServiceError("unpopular.character", "too few responses"))
      case Right(s) =>  Right(s.characters.head)
      case Left(e) => Left(e)
    }
  }
}