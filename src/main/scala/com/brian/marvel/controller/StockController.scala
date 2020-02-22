package com.brian.marvel.controller

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.stream.scaladsl.{FileIO, Framing}
import akka.util.ByteString
import com.brian.marvel.domain.{HeightMapRequest, SetTickerRequest}
import com.brian.marvel.service.StockTickerService
import com.brian.marvel.utils.Controller

import scala.concurrent.Future
import scala.util.control.NonFatal
import scala.util.{Failure, Success}

class StockController(tickerService: StockTickerService, heightMapGenerator: HeightMapGenerator) extends Controller {

  private lazy val getTickers = (get & path("ticker") & pathEnd) {
    completion(tickerService.getActiveTickers)
  }

  private lazy val setTickers = (post & path("ticker") & pathEnd) {
    entity(as[SetTickerRequest]) { setTickerRequest =>
      completion(tickerService.setActiveTickers(setTickerRequest))
    }
  }

  private lazy val getMapRandom = (post & path("height")) {
    entity(as[HeightMapRequest]) { heightMapRequest =>
        val file = heightMapGenerator
        .genHeightMap(heightMapRequest.width, heightMapRequest.height)

      file match {
        case Success(value) => getFromFile(value)
        case Failure(NonFatal(e)) => throw e

      }
    }
  }

  private lazy val buildMapRandom = (post & path("height-comp")) {

    fileUpload("file") { case (metadata, byteSource) =>
      headerValueByName("Content-Length") { fileSize =>

        val file = heightMapGenerator
          .fromFileUpload(metadata, byteSource)

          complete(fileSize)
      }
    }

  }

  lazy val route: Route = pathPrefix("stock") {
    getTickers ~ setTickers ~ getMapRandom ~ buildMapRandom
  }
}
