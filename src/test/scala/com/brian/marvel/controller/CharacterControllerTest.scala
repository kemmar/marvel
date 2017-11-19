package com.brian.marvel.controller

import akka.http.scaladsl.model.StatusCodes
import com.brian.marvel.controller.stubs.{GoogleTranslateStubs, MarvelStubs}
import com.brian.marvel.controller.utils.TestCommons
import play.api.libs.json.JsValue
import play.api.libs.json.Json._

class CharacterControllerTest extends TestCommons {

  before {
    MarvelStubs.restart
    GoogleTranslateStubs.restart
  }

  it should "return a list of character IDs" in {
    MarvelStubs.returnSuccessfulCharacters

    Get("/characters") ~> routes ~> check {
      status shouldBe StatusCodes.OK
      responseAs[JsValue] shouldEqual arr(1011334, 1017100, 1009144, 1010699, 1009146)
    }
  }

  it should "return a characters information" in {
    MarvelStubs.returnSuccessfulCharacter("1011334")

    Get("/characters/1011334") ~> routes ~> check {
      status shouldBe StatusCodes.OK
      responseAs[JsValue] shouldEqual obj(
        "id" -> 1011334,
        "name" -> "3-D Man",
        "description" -> "",
        "thumbnail" -> obj(
          "path" -> "http://i.annihil.us/u/prod/marvel/i/mg/c/e0/535fecbbb9784",
          "extension" -> "jpg"
        )
      )
    }
  }

  it should "return the characters wiki" in {
    MarvelStubs.returnSuccessfulCharacter("1011334")
    MarvelStubs.returnSuccessfulWiki("1011334")

    Get("/characters/1011334/powers") ~> routes ~> check {
      status shouldBe StatusCodes.OK
      responseAs[JsValue] shouldEqual parse("""{
                                              |    "language": "en",
                                              |    "powers": "Amazing"
                                              |}""".stripMargin)
    }
  }

  it should "return the characters wiki in given language" in {
    MarvelStubs.returnSuccessfulCharacter("1011334")
    MarvelStubs.returnSuccessfulWiki("1011334")
    GoogleTranslateStubs.returnSuccessfulTranslation

    Get("/characters/1011334/powers?language=fr") ~> routes ~> check {
      status shouldBe StatusCodes.OK
      responseAs[JsValue] shouldEqual parse("""{
                                              |    "language": "fr",
                                              |    "powers": "Incroyable"
                                              |}""".stripMargin)
    }
  }

  it should "handle errors " in {
    val error = "something has gone wrong"
    MarvelStubs.failWithError(error, error)

    Get("/characters") ~> routes ~> check {
      status shouldBe StatusCodes.BadRequest
      responseAs[JsValue].toString shouldEqual obj(
        "code" -> error,
        "message" -> error
      ).toString
    }
  }

}
