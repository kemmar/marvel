package com.brian.marvel.endpoints

import cats.implicits._
import akka.actor.ActorSystem
import akka.http.scaladsl.model.{HttpRequest, Uri}
import akka.stream.Materializer
import com.brian.marvel.domain.MarvelCharacter
import com.brian.marvel.utils.ResponseHandler
import com.brian.marvel.utils.ResponseHandler.ResponseType
import com.typesafe.config.Config

import scala.xml.XML
import scala.concurrent.ExecutionContext.Implicits.global

class GetPowersEndpoint(implicit as: ActorSystem, mat: Materializer, conf: Config) extends ResponseHandler {

  lazy val apiKey = conf.getString("google.apiKey")

  def getPowersTranslated(character: MarvelCharacter, language: String) = {
    val path = s"/language/translate/v2?q=${character.wiki.map(scrapePowers).getOrElse("information not available")}&target=$language&key=$apiKey"
    val url: Uri = conf.getString("marvel.url") + path

    val req = HttpRequest(uri = url)

    sendLoggedRequest(req).as[String]
  }

  private def scrapePowers(url: String): ResponseType[String] = {
    val req = HttpRequest(uri = url)

    for {
     resp <- sendLoggedRequest(req).as[String]
    } yield (XML.loadString(resp) \\ "div").filter(_.attributes.exists(_.value.text == "char-powers-content")).text
  }
}