package com.brian.marvel.domain

import play.api.libs.json.{JsPath, Json, Reads, Writes}
import play.api.libs.functional.syntax._

case class GoogleError(code: String, message: String) extends ErrorBase

object GoogleError {
  implicit val googleErrorWrites = new Writes[GoogleError] {
    def writes(error: GoogleError) = Json.obj(
      "code" -> error.code,
      "message" -> error.message
    )
  }

  implicit val googleErrorReads: Reads[GoogleError] = (
    (JsPath \ 'error \ 'status).read[String] and
    (JsPath \ 'error \ 'message).read[String]
  )(GoogleError.apply _)
}