package com.brian.marvel.domain

import play.api.libs.json.Json

case class HeightMapRequest(width: Int, height: Int)

object HeightMapRequest {
  implicit val setTickerFormat = Json.format[HeightMapRequest]
}

