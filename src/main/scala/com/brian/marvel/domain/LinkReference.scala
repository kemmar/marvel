package com.brian.marvel.domain

import de.heikoseeberger.akkahttpplayjson.PlayJsonSupport
import play.api.libs.json.Json

case class LinkReference(href: Seq[String])

object LinkReference extends PlayJsonSupport {
  implicit val format = Json.format[LinkReference]
}