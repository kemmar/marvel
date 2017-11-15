package com.brian.marvel.controller.stubs

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.core.WireMockConfiguration._

class MockServer(host: String = "localhost", port: Int, securePort: Option[Int] = None) {

  val config = wireMockConfig().port(port)
  securePort.foreach(config.httpsPort(_))

  lazy val wireMockServer: WireMockServer = new WireMockServer(config)

  def start(): Unit = {
    if (!wireMockServer.isRunning) {
      wireMockServer.start()
    }
  }

  def switchToMe() = {
    WireMock.configureFor(host, port)
  }

  def die() : Unit = {
    if (wireMockServer.isRunning) {
      wireMockServer.stop()
    }
  }

  def reset(): Unit = {
    switchToMe()
    WireMock.reset()
  }

  def restart(): Unit = {
    start()
    reset()
  }
}


