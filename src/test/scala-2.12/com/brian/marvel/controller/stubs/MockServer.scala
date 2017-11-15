package com.brian.marvel.controller.stubs

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.core.WireMockConfiguration._

abstract class MockServer(host: String = "localhost", port: Int) extends WireMockServer(wireMockConfig().port(port)) {
  WireMock.configureFor(host, port)
}


