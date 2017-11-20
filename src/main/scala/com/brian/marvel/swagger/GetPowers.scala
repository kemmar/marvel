package com.brian.marvel.swagger

import javax.ws.rs.Path

import com.brian.marvel.domain.Powers
import io.swagger.annotations._

@Api(value = "characters", produces = "application/json")
@Path("/characters/{ characterId }/powers")
trait GetPowers {

  @ApiOperation(value = "Returns the characters powers in the given language", httpMethod = "GET")
  @ApiImplicitParams(
    Array(
    new ApiImplicitParam(
      name = "language",
      value = "ISO language code",
      required = false,
      dataType = "string",
      defaultValue = "en",
      paramType = "query"),
    new ApiImplicitParam(
      name = "characterId",
      value = "character id",
      required = true,
      dataType = "string",
      defaultValue = "1011334",
      paramType = "path")
  ))
  @ApiResponses(Array(
    new ApiResponse(code = 200, message = "Return powers in given language", response = classOf[Powers]),
    new ApiResponse(code = 500, message = "Internal server error")
  ))
  def dummyGetRoute: Unit

}
