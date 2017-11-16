package com.brian.marvel.domain

import play.api.libs.json.Json

case class Thumbnail(path: String, extension: String)

object Thumbnail {
  implicit val formatThumbnail = Json.format[Thumbnail]
}