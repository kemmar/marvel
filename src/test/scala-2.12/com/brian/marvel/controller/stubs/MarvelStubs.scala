package com.brian.marvel.controller.stubs

import com.github.tomakehurst.wiremock.client.WireMock._

object MarvelStubs extends MockServer(port = 1234) {

  def returnSuccessfulCharacters = {

    stubFor(get(urlEqualTo("/v1/public/characters"))
      .willReturn(aResponse()
        .withStatus(200)
        .withBodyFile("/src/test/resources/stubs/exampleResponse.json")))
  }

  def failWithError(code: String, message: String, statusCode: Int = 400) = {
    stubFor(get(urlEqualTo("/v1/public/characters"))
      .willReturn(aResponse()
        .withStatus(statusCode).withBody(
        s"""{
           |    "code": "$code",
           |    "message": "$message"
           |}""".stripMargin)))
  }
}
