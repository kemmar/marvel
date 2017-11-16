package com.brian.marvel.domain

import play.api.libs.functional.syntax._
import play.api.libs.json.{JsPath, Reads}

case class CharacterResponse(offset: Int, total: Int, characters: Seq[MarvelCharacter])

object CharacterResponse {
  implicit val readCharacterResponse: Reads[CharacterResponse] = (
    (JsPath \ 'data \ 'offset).read[Int] and
    (JsPath \ 'data \ 'total).read[Int] and
    (JsPath \ 'data \ 'results).read[Seq[MarvelCharacter]]
  )(CharacterResponse.apply _)
}