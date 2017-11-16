package com.brian.marvel.service

import com.brian.marvel.domain.ErrorObj

object ErrorConstants {

  val ServiceError = ErrorObj("service.cannot.be.called", "service is down or returning service error")

}
