package com.brian.marvel.domain

import com.brian.marvel.utils.RedirectResponse
import play.api.libs.functional.syntax._
import play.api.libs.json._

case class MarvelCharacter(id: Int, name: String, description: String, thumbnail: Thumbnail, wiki: Option[String]) {
  private def translateLink(lng: String) = wiki.map(l => s"http://translate.google.com/translate?js=n&sl=auto&tl=$lng&u=$l")

  private def buildRedirectResponse(linkOpt: Option[String]): Option[RedirectResponse] = linkOpt match {
    case Some(link) => Some(RedirectResponse(link))
    case None => None
  }

  def wikiRedirect(language: String): Option[RedirectResponse] = language match {
    case lng if lng.toLowerCase == "en" => buildRedirectResponse(wiki)
    case lng => buildRedirectResponse(translateLink(lng.toLowerCase))
  }
}

object MarvelCharacter {
  implicit val formatMarvelCharacter: Writes[MarvelCharacter] = Json.format[MarvelCharacter]

  case class Hateos(`type`: String, url: String)

  object Hateos {
    implicit val hateosFormat = Json.format[Hateos]
  }

  implicit val readMarvelCharacter: Reads[MarvelCharacter] = (
    (JsPath \ 'id).read[Int] and
      (JsPath \ 'name ).read[String] and
      (JsPath \ 'description ).read[String] and
      (JsPath \ 'thumbnail ).read[Thumbnail] and
      (JsPath \ 'urls  ).read[Seq[Hateos]].map(links => links.find(_.`type`.toLowerCase == "wiki").map(_.url))
    ) (MarvelCharacter.apply _)
}
