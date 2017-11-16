package com.brian.marvel.utils

import java.security.MessageDigest

import com.typesafe.config.{Config, ConfigObject}

object Authenticator {

  def makeHash(service: String)(implicit conf: Config) = {
    val ts = System.currentTimeMillis().toString
    val privateKey = conf.getString(s"$service.privateKey")
    val publicKey = conf.getString(s"$service.publicKey")

    (ts, MessageDigest
      .getInstance("MD5")
      .digest((ts+privateKey+publicKey).getBytes)
      .map { "%02x".format(_) }
      .mkString(""))
  }

}
