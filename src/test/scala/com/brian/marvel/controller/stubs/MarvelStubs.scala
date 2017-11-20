package com.brian.marvel.controller.stubs

import com.github.tomakehurst.wiremock.client.WireMock._

object MarvelStubs extends MockServer(port = 8090, securePort = Some(8490)) {

  def returnSuccessfulCharacters = {
    switchToMe()
    stubFor(get(urlPathEqualTo("/v1/public/characters"))
      .willReturn(aResponse()
        .withStatus(200)
        .withHeader("Content-Type", "application/json")
        .withBodyFile("/stubs/exampleResponse.json")))
  }

  def returnSuccessfulCharacter(charId: String) = {
    switchToMe()
    stubFor(get(urlPathEqualTo(s"/v1/public/characters/$charId"))
      .willReturn(aResponse()
        .withStatus(200)
        .withHeader("Content-Type", "application/json")
        .withBodyFile("/stubs/exampleCharacterResponse.json")))
  }

  def returnSuccessfulWiki(charId: String) = {
    switchToMe()
    stubFor(get(urlPathEqualTo(s"/$charId"))
      .willReturn(aResponse()
        .withStatus(200)
        .withHeader("Content-Type", "text/html")
        .withBodyFile("/stubs/CharactorBio.html")))
  }

  def failWithError(code: Int, message: String, statusCode: Int = 400) = {
    switchToMe()
    stubFor(get(urlPathEqualTo("/v1/public/characters"))
      .willReturn(aResponse()
        .withStatus(statusCode)
        .withHeader("Content-Type", "application/json")
        .withBody(
          s"""{
             |    "code": $code,
             |    "status": "$message"
             |}""".stripMargin)))
  }
}
