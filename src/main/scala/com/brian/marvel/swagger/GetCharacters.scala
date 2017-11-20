package com.brian.marvel.swagger

import javax.ws.rs.Path

import io.swagger.annotations.{Api, ApiOperation, ApiResponse, ApiResponses}

@Api(value = "characters", produces = "application/json")
@Path("/characters")
trait GetCharacters {

  @ApiOperation(value = "Returns a list of characters as IDs", httpMethod = "GET")
  @ApiResponses(Array(
    new ApiResponse(code = 200, message = "Return powers in given language", response = classOf[Seq[Int]]),
    new ApiResponse(code = 500, message = "Internal server error")
  ))
  def dummyGetRoute: Unit

}
