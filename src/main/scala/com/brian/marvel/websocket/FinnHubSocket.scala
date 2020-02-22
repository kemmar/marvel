package com.brian.marvel.websocket

import akka.actor.ActorSystem
import akka.stream.Materializer
import com.brian.marvel.utils.ResponseHandler._
import com.typesafe.config.Config

class FinnHubSocket(implicit as: ActorSystem,
                    mat: Materializer, conf: Config) {

  lazy val apiKey = conf.getString("finnhub.apiKey")
  lazy val socketHost = conf.getString("finnhub.socket")

  def getPowersTranslated(ticker: String): ResponseType[String] = {

    ""
  }

}