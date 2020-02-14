package com.brian.marvel.domain

import de.heikoseeberger.akkahttpplayjson.PlayJsonSupport
import play.api.libs.json.Json

case class WuxiaNovel(title: String, description: String, image: String, pages: LinkReference)

object WuxiaNovel extends PlayJsonSupport {
  implicit val format = Json.format[WuxiaNovel]
}
