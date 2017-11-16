package com.brian.marvel.endpoints

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{HttpRequest, Uri}
import akka.stream.Materializer
import com.brian.marvel.domain.{CharacterResponse, ErrorBase, MarvelCharacter}
import com.brian.marvel.utils.ResponseHandler.ResponseType
import com.brian.marvel.utils.{Authenticator, ResponseHandler}
import com.typesafe.config.Config

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class GetCharacterEndpoint(implicit as: ActorSystem, mat: Materializer, conf: Config) extends ResponseHandler {

  val (stamp, hash) = Authenticator.makeHash("marvel")

  val apiKey = conf.getString("marvel.apiKey")

  def getCharacter(id: String): ResponseType[MarvelCharacter] = {

    val path = s"/v1/public/characters/$id"
    val url: Uri = conf.getString("marvel.url") + path
    val req = HttpRequest(uri = url.withRawQueryString(s"apikey=$apiKey&hash=$hash&ts=$stamp"))

    sendLoggedRequest(req).as[CharacterResponse].map(_.map(_.characters.head))
  }

}