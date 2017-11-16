package com.brian.marvel.utils

import play.api.libs.json.Json

case class RedirectResponse(url: String)

object RedirectResponse {
  implicit val redirectResponseFormat = Json.format[RedirectResponse]
}
