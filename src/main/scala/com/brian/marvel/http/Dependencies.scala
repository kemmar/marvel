package com.brian.marvel.http

import akka.actor.ActorSystem
import akka.stream.SystemMaterializer
import com.brian.marvel.controller.{HeightMapGenerator, StockController}
import com.brian.marvel.endpoints.FinnHubEndpoint
import com.brian.marvel.service.StockTickerService
import com.brian.marvel.utils.ErrorHandlerTrait
import com.brian.marvel.websocket.FinnHubSocket
import com.github.swagger.akka.SwaggerSite
import com.typesafe.config.ConfigFactory

trait Dependencies extends ErrorHandlerTrait with SwaggerSite with Cache {

  implicit val config = ConfigFactory.load()
  implicit val system: ActorSystem
  implicit val materializer: SystemMaterializer

  lazy val finnHubSocket = new FinnHubSocket()

  lazy val finnHubEndpoint = new FinnHubEndpoint()

  lazy val tickerService = new StockTickerService(finnHubEndpoint)

  lazy val heightMapGenerator = new HeightMapGenerator()

  lazy val stockController = new StockController(tickerService, heightMapGenerator)

  lazy val routes = (handleRejections(myRejectionHandler) & handleExceptions(myExceptionHandler)) {
    stockController.route
  }

}
