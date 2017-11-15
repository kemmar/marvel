package com.brian.marvel.endpoints

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.HttpRequest
import akka.stream.Materializer
import com.brian.marvel.domain.ErrorObj
import com.brian.marvel.utils.ResponseHandler
import play.api.libs.json.JsValue

import scala.concurrent.Future

class GetCharactersEndpoint(implicit as: ActorSystem, mat: Materializer) extends ResponseHandler {

  def getCharacters: Future[Either[ErrorObj, JsValue]] = {
    val req = HttpRequest(uri = "https://gateway.marvel.com/v1/public/characters")

    Http().singleRequest(req).as[JsValue]
  }
}
