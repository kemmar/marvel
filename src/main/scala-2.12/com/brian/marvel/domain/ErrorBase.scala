package com.brian.marvel.domain

import play.api.libs.json.{Json, Writes}

abstract class ErrorBase() {
  val code: String
  val message: String

}

object ErrorBase {
  implicit val errorBaseWrites = new Writes[ErrorBase] {
    def writes(error: ErrorBase) = Json.obj(
      "code" -> error.code,
      "message" -> error.message
    )
  }
}
