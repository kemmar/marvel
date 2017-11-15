package com.brian.marvel.controller

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.testkit.ScalatestRouteTest
import com.brian.marvel.controller.stubs.MarvelStubs
import com.brian.marvel.service.Dependencies
import de.heikoseeberger.akkahttpplayjson.PlayJsonSupport
import org.scalatest.{BeforeAndAfter, FlatSpec, Matchers}
import play.api.libs.json.JsValue
import play.api.libs.json.Json._

class CharacterControllerTest extends FlatSpec with Matchers with BeforeAndAfter with ScalatestRouteTest with Dependencies with PlayJsonSupport {

  before {
    MarvelStubs.resetAll()
  }

  it should "return a list of charecter IDs" in {
    MarvelStubs.returnSuccessfulCharacters

    Get("characters") ~> routes ~> check {
      status shouldBe StatusCodes.OK
      responseAs[JsValue] shouldEqual arr("1011334", "1017100", "1009144", "1010699", "1009146")
    }
  }

  it should "handle errors " in {
    val error = "something has gone wrong"
    MarvelStubs.failWithError(error, error)

    Get("characters") ~> routes ~> check {
      status shouldBe StatusCodes.OK
      responseAs[JsValue] shouldEqual obj(
        "code" -> error,
        "message" -> error
      )
    }
  }

}
