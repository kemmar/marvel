package com.brian.marvel.db.entity

import play.api.libs.json.Json

case class BinaryEntity(
                       id: Int,
                       byteX: Int,
                       byteB: Int,
                       isPositive: Boolean
                       )

object BinaryEntity {
  implicit val jsonBinaryEntity = Json.format[BinaryEntity]
}
