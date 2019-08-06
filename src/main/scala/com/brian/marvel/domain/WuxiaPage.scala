package com.brian.marvel.domain

import de.heikoseeberger.akkahttpplayjson.PlayJsonSupport
import play.api.libs.json.Json

case class WuxiaPage(title: String, text: String, nextLink: Option[String])


object WuxiaPage extends PlayJsonSupport {

  implicit val format = Json.format[WuxiaPage]

}