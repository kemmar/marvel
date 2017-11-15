package com.brian.marvel.domain

import play.api.libs.json.Json

case class ErrorObj(code: String, message: String) extends ErrorBase

object ErrorObj {
  implicit val errorObjFormat = Json.format[ErrorObj]
}
