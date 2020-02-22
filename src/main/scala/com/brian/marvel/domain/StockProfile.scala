package com.brian.marvel.domain

import play.api.libs.json.{JsPath, Json, Reads, Writes}
import play.api.libs.functional.syntax._

case class StockProfile(
                         address: String,
                         city: String,
                         country: String,
                         currency: String,
                         cusip: String,
                         description: String,
                         exchange: String,
                         ggroup: String,
                         gind: String,
                         gsector: String,
                         gsubind: String,
                         ipo: String,
                         isin: String,
                         naics: String,
                         name: String,
                         phone: String,
                         state: String,
                         ticker: String,
                         weburl: String
                       )

object StockProfile {
  implicit val stockProfileFormat = Json.format[StockProfile]
}
