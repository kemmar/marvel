package com.brian.marvel.swagger

import com.github.swagger.akka.SwaggerHttpService

object Swagger extends SwaggerHttpService {
  override def apiClasses: Set[Class[_]] = Set(
    classOf[GetPowers],
    classOf[GetCharacters],
    classOf[GetCharacter]
  )
}