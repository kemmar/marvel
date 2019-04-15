package com.brian.marvel.utils
import org.flywaydb.core.Flyway
import slick.jdbc.JdbcBackend.Database

final case class DBConfiguration(username: String = "root", password: String = "") {

  lazy val formatedUrl: String =
    s"jdbc:mysql://localhost:3306/akk-fsm-spike?autoReconnect=true&useSSL=false&createDatabaseIfNotExist=true"

  def flyway =
    Flyway.configure
      .dataSource(
        formatedUrl,
        username,
        password
      )
      .load

  val database: Database = {

    Database.forURL(
      url = formatedUrl,
      user = username,
      password = password
    )
  }
}
