package com.brian.marvel.controller

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.stream.Materializer
import com.brian.marvel.service.{EpubService, WuxiaService}
import com.brian.marvel.utils.Controller
import io.swagger.annotations._
import javax.ws.rs.Path

import scala.concurrent.ExecutionContext

@Api(value = "/web-novel/wuxia", produces = "application/json")
@Path("/web-novel/wuxia")
class WuxiaController()(implicit ec: ExecutionContext, mat: Materializer) extends Controller {

  private lazy val wuxia = path("wuxia") {
    complete {
      EpubService.createBook {
        WuxiaService
          .buildNovelInformation("https://boxnovel.com/novel/ultimate-scheming-system/")
      }
    }
  }

  lazy val route: Route = pathPrefix("web-novel") {
    wuxia
  }
}