package com.brian.marvel.controller.stubs

import com.github.tomakehurst.wiremock.client.WireMock._

object GoogleTranslateStubs extends MockServer(port = 8095, securePort = Some(8495)) {

  def returnSuccessfulTranslation = {
    switchToMe()
    stubFor(get(urlPathEqualTo("/language/translate/v2"))
      .willReturn(aResponse()
        .withStatus(200)
        .withHeader("Content-Type", "application/json")
        .withBody("""{
                    |    "data": {
                    |        "translations": [
                    |            {
                    |                "translatedText": "Incroyable",
                    |                "detectedSourceLanguage": "en"
                    |            }
                    |        ]
                    |    }
                    |}""".stripMargin)))
  }

}
