package com.brian.marvel.domain

import play.api.libs.json.Json

case class MarvelCharacter(id: Int, name: String, description: String, thumbnail: Thumbnail)

object MarvelCharacter {
implicit val formatMarvelCharacter = Json.format[MarvelCharacter]
}
