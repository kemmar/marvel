package com.brian.marvel.controller.utils

import akka.http.scaladsl.testkit.ScalatestRouteTest
import com.brian.marvel.service.Dependencies
import de.heikoseeberger.akkahttpplayjson.PlayJsonSupport
import org.scalatest.{BeforeAndAfter, FlatSpec, Matchers}

trait TestCommons extends FlatSpec with Matchers with BeforeAndAfter with ScalatestRouteTest with Dependencies with PlayJsonSupport
