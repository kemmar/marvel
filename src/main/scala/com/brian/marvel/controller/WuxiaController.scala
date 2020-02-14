package com.brian.marvel.controller

import akka.actor.ActorSystem
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.stream.{ActorMaterializer, Materializer}
import com.brian.marvel.service.{EpubService, WuxiaService}
import com.brian.marvel.utils.Controller
import io.swagger.annotations._
import javax.ws.rs.Path

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext}

@Api(value = "/web-novel/wuxia", produces = "application/json")
@Path("/web-novel/wuxia")
class WuxiaController()(implicit ec: ExecutionContext, mat: Materializer) extends Controller {

  private lazy val wuxia = (path("wuxia") & pathEnd) {
    complete {
      EpubService.createBook {
        WuxiaService
          .buildNovelInformation("https://boxnovel.com/novel/ultimate-scheming-system/")
      }
    }
  }

  private lazy val wuxiaPages = (path("wuxia" / "pages") & pathEnd) {
    complete {
        WuxiaService
          .findPages("https://boxnovel.com/novel/ultimate-scheming-system/")
    }
  }

  lazy val route: Route = pathPrefix("web-novel") {
    wuxia ~ wuxiaPages
  }
}

object Test extends App {

  import scala.concurrent.ExecutionContext.Implicits.global

  implicit val system = ActorSystem()
  implicit val materializer = ActorMaterializer()

  val f = EpubService.createBook {
    WuxiaService
      .buildNovelInformation("https://boxnovel.com/novel/ultimate-scheming-system/")
  }

  Await.result(f, Duration.Inf)
}
