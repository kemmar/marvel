package com.brian.marvel.domain

import play.api.libs.json.{JsPath, Reads}

case class Translation(translation: Seq[Powers])

object Translation {
  implicit val translationReads: Reads[Translation] =
    (JsPath \ 'data \ 'translations).read[Seq[Powers]].map(Translation( _))
}