package com.brian.marvel.http

import com.brian.marvel.domain.ServiceError

object ErrorConstants {

  val ServiceErrorConstant = ServiceError("service.cannot.be.called", "service is down or returning service error")

}
