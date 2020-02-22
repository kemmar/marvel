package com.brian.marvel.domain

import play.api.libs.json.Json

case class SetTickerRequest(ticker: String)

object SetTickerRequest {
  implicit val setTickerFormat = Json.format[SetTickerRequest]
}