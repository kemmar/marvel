package com.brian.marvel.endpoints


import java.net.{URL, URLEncoder}

import akka.actor.ActorSystem
import akka.http.scaladsl.model.headers.Accept
import akka.http.scaladsl.model.{ContentTypes, HttpRequest, Uri}
import akka.stream.Materializer
import cats.implicits._
import com.brian.marvel.domain._
import com.brian.marvel.utils.ResponseHandler
import com.brian.marvel.utils.ResponseHandler._
import com.typesafe.config.Config
import org.htmlcleaner.HtmlCleaner

import scala.concurrent.ExecutionContext.Implicits.global

class GetPowersEndpoint(implicit as: ActorSystem, mat: Materializer, conf: Config) extends ResponseHandler {

  lazy val apiKey = conf.getString("google.apiKey")

  def getPowersTranslated(character: MarvelCharacter, language: String): ResponseType[Powers] = {

    character.wiki.map(scrapePowers) match {
      case Some(power) => {
        for {
          p <- power
          t <- translate(p, language)
        } yield t
      }
      case None => translate("information not available", language)
    }
  }

  private def scrapePowers(url: String): ResponseType[String] = {
    val cleaner = new HtmlCleaner
    val rootNode = cleaner.clean(new URL(url))
    val elements = rootNode.getElementsByAttValue("id", "char-powers-content", true, false)

    elements.map(_.getText.toString).mkString(". ").asRight
  }

  def translate(text: String, lang: String): ResponseType[Powers] = {
    if (!lang.equals("en")) {

      val encText = URLEncoder.encode(text, "UTF-8")
      val path = s"/language/translate/v2"
      val url: Uri = conf.getString("google.url") + path

      val req = HttpRequest(uri = url.withRawQueryString(s"q=$encText&target=$lang&key=$apiKey"))
        .addHeader(Accept(ContentTypes.`application/json`.mediaType))

      sendLoggedRequest(req).as[Translation, GoogleError].map(_.translation.head.copy(language = lang))
    } else Powers(lang, text)
  }

}