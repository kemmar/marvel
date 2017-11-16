package com.brian.marvel.domain

import play.api.libs.json.Json

case class ServiceError(code: String, message: String, statusCode: Int = 422) extends ErrorBase {
  def toStandardError: ErrorObj = ErrorObj(code, message)
}

object ServiceError {
  implicit val formatServiceError = Json.format[ServiceError]
}