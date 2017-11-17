package com.brian.marvel.domain

import play.api.libs.json.{JsPath, Json, Reads, Writes}
import play.api.libs.functional.syntax._

case class Powers(language: String, powers: String)

object Powers {
  implicit val powersFormat: Writes[Powers] = (powers: Powers) => Json.obj(
    "language" -> powers.language,
    "powers" -> powers.powers
  )

  implicit val errorBaseReads: Reads[Powers] = (
    (JsPath \ 'detectedSourceLanguage).read[String] and
      (JsPath \ 'translatedText).read[String]
    ) (Powers.apply _)
}
