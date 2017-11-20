package com.brian.marvel.service

import java.util.concurrent.TimeUnit

import com.google.common.cache.CacheBuilder

trait Cache {

  def modelCache[A, B] = CacheBuilder.newBuilder()
    .maximumSize(600)
    .expireAfterWrite(24, TimeUnit.HOURS)

}
