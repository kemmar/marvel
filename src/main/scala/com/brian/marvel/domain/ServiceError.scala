package com.brian.marvel.domain

import play.api.libs.functional.syntax._
import play.api.libs.json.{JsPath, Json, Reads}

case class ServiceError(code: String, message: String, statusCode: Option[Int] = None) extends ErrorBase {
  def toStandardError: ErrorObj = ErrorObj(code, message)
}

object ServiceError {

  val readsServiceError: Reads[ServiceError] = (
    (JsPath \ 'code).read[Int].map(_.toString) and
      (JsPath \ 'message).read[String] and
      (JsPath \ 'statusCode).readNullable[Int]
    ) (ServiceError.apply _)

  implicit val formatServiceError =
    Json.format[ServiceError]
      .orElse(readsServiceError)
}