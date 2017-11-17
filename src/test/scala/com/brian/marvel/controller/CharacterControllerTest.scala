package com.brian.marvel.controller

import akka.http.scaladsl.model.StatusCodes
import com.brian.marvel.controller.stubs.MarvelStubs
import com.brian.marvel.controller.utils.TestCommons
import play.api.libs.json.JsValue
import play.api.libs.json.Json._

class CharacterControllerTest extends TestCommons {

  before {
    MarvelStubs.restart()
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
        ),
        "wiki" -> "http://localhost:8090/1011334"
      )
    }
  }

  it should "return the characters wiki" in {
    MarvelStubs.returnSuccessfulCharacter("1011334")
    MarvelStubs.returnSuccessfulWiki("1011334")

    Get("/characters/1011334/powers") ~> routes ~> check {
      responseAs[JsValue] shouldEqual obj(
        "language" -> "en",
        "powers" ->
          """Through concentration,
            |Hal could merge the images of his brother imprinted on his glasses and thus cause his
            |brother Chuck to reappear as a three-dimensional man, clad in an altered version of his
            |experimental flight suit and endowed with physical abilities roughly three times greater
            |than those of an ordinary human. Hal would fall into a trance-like state when Chuck
            |appeared, and Chuck could only exist in the three-dimensional world for three hours at a
            |time, after which Hal had to revive.
            |As the 3-D Man, Chandler possessed roughly three times the physical abilities and
            |sensory acuity of an ordinary human in peak condition and is capable of slightly
            |superhuman strength and speed. His stamina, durability, agility and reflexes are also
            |estimated to be superhuman, namely roughly triple that of a human in peak physical
            |condition. He could also sense"""
      )
      status shouldBe StatusCodes.OK
    }
  }

  it should "return the characters wiki in given language" in {
    MarvelStubs.returnSuccessfulCharacter("1011334")
    MarvelStubs.returnSuccessfulWiki("1011334")

    Get("/characters/1011334/powers?language=fr") ~> routes ~> check {
      status shouldBe StatusCodes.OK
      responseAs[JsValue] shouldEqual obj(
        "language" -> "fr",
        "powers" ->
            """Grâce à la concentration,
              |Hal pourrait fusionner les images de son frère imprimées sur ses lunettes et provoquer ainsi son
              |frère Chuck à réapparaître en tant qu'homme en trois dimensions, vêtu d'une version altérée de son
              |combinaison de vol expérimentale et dotée de capacités physiques environ trois fois plus grande
              |que ceux d'un humain ordinaire. Hal tomberait dans un état de transe quand Chuck
              |apparu, et Chuck ne pouvait exister dans le monde en trois dimensions pendant trois heures à
              |temps, après quoi Hal a dû faire revivre.
              |En tant qu'homme en 3D, Chandler possédait à peu près trois fois les capacités physiques et
              |l'acuité sensorielle d'un humain ordinaire en état de pointe et est capable de légèrement
              |force et vitesse surhumaines. Son endurance, sa durabilité, son agilité et ses réflexes sont aussi
              |estimé être surhumain, à savoir à peu près le triple de celui d'un humain en pic physique
              |condition. Il pouvait aussi sentir"""
      )
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
