package com.brian.marvel.service

import com.brian.marvel.domain.{SetTickerRequest, StockProfile}
import com.brian.marvel.endpoints.FinnHubEndpoint
import com.brian.marvel.utils.ResponseHandler.ResponseType

class StockTickerService(finnHubEndpoint: FinnHubEndpoint) {
  def getActiveTickers: ResponseType[Seq[String]] = ???

  def setActiveTickers(setTickerRequest: SetTickerRequest): ResponseType[StockProfile] = {
    finnHubEndpoint.validateTicker(setTickerRequest.ticker)
  }
}
