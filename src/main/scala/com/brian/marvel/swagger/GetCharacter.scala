package com.brian.marvel.swagger

import javax.ws.rs.Path

import com.brian.marvel.domain.MarvelCharacter
import io.swagger.annotations._

@Api(value = "characters", produces = "application/json")
@Path("/characters/{characterId}")
trait GetCharacter {

  @ApiOperation(value = "Returns the character given", httpMethod = "GET")
  @ApiImplicitParams(
    Array(
      new ApiImplicitParam(
        name = "characterId",
        value = "character id",
        required = true,
        dataType = "string",
        defaultValue = "1011334",
        paramType = "path")
    ))
  @ApiResponses(Array(
    new ApiResponse(code = 200, message = "Return a single character", response = classOf[MarvelCharacter]),
    new ApiResponse(code = 500, message = "Internal server error")
  ))
  def dummyGetRoute: Unit
}
