package com.brian.marvel.endpoints

import akka.actor.ActorSystem
import akka.http.scaladsl.model.{ContentTypes, HttpRequest, Uri}
import akka.http.scaladsl.model.headers.Accept
import akka.stream.Materializer
import com.brian.marvel.domain.{ServiceError, StockProfile}
import com.brian.marvel.utils.ResponseHandler
import com.brian.marvel.utils.ResponseHandler._
import com.typesafe.config.Config

class FinnHubEndpoint()(implicit as: ActorSystem,
                        mat: Materializer, conf: Config) extends ResponseHandler {

  lazy val apiKey = conf.getString("finnhub.apiKey")
  lazy val hostUrl = conf.getString("finnhub.url")

  def validateTicker(ticker: String): ResponseType[StockProfile] = {

    val path = s"/stock/profile"

    val url: Uri = hostUrl + path

    val req =
      HttpRequest(uri = url.withRawQueryString(s"symbol=${ticker.toUpperCase}&token=$apiKey"))
      .addHeader(Accept(ContentTypes.`application/json`.mediaType))

    sendLoggedRequest(req)
      .as[StockProfile, ServiceError]
  }

}
