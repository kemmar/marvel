package com.brian.marvel.domain

import io.swagger.annotations.{ApiModel, ApiModelProperty}
import play.api.libs.functional.syntax._
import play.api.libs.json._

import scala.annotation.meta.field

@ApiModel(description = "MarvelCharacter")
case class MarvelCharacter(id: Int,
                           name: String,
                           description: String,
                           thumbnail: Thumbnail,
                           @(ApiModelProperty @field)(hidden=true)
                           wiki: Option[String])

object MarvelCharacter {
  implicit val formatMarvelCharacter: Writes[MarvelCharacter] = Json.format[MarvelCharacter]

  case class Hateos(`type`: String, url: String)

  object Hateos {
    implicit val hateosFormat = Json.format[Hateos]
  }

  implicit val readMarvelCharacter: Reads[MarvelCharacter] = (
    (JsPath \ 'id).read[Int] and
      (JsPath \ 'name ).read[String] and
      (JsPath \ 'description ).read[String] and
      (JsPath \ 'thumbnail ).read[Thumbnail] and
      (JsPath \ 'urls  ).read[Seq[Hateos]].map(links => links.find(_.`type`.toLowerCase == "wiki").map(_.url))
    ) (MarvelCharacter.apply _)
}
