package com.brian.marvel.endpoints

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.HttpRequest
import akka.stream.Materializer
import com.brian.marvel.utils.ResponseHandler
import com.brian.marvel.utils.ResponseHandler.ResponseType
import com.typesafe.config.Config
import play.api.libs.json.JsValue

class GetCharactersEndpoint(implicit as: ActorSystem, mat: Materializer, conf: Config) extends ResponseHandler {

  val url = conf.getString("marvel.url")
  val path = "/v1/public/characters"

  def getCharacters: ResponseType[JsValue] = {
    val req = HttpRequest(uri = url + path)

    Http().singleRequest(req).as[JsValue]
  }
}
