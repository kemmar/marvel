package com.brian.marvel


import com.brian.marvel.StatusRepository.StatusChange
import slick.dbio.{Effect, NoStream}
import slick.jdbc.JdbcBackend.Database
import slick.jdbc.MySQLProfile.api._
import slick.sql.FixedSqlAction

import scala.util.{Failure, Success}
import scala.concurrent.ExecutionContext.Implicits.global

class StatusRepository(db: Database) {

  val statusQuery = TableQuery[TestTable]

  def updateStatus(status: StatusChange): FixedSqlAction[Int, NoStream, Effect.Write] = {

    println(status.status)
    Thread.sleep(500)

    statusQuery
      .insertOrUpdate(status.toEntity)
  }

  def executeUpdate[T, S <: NoStream, E <: Effect](data: DBIOAction[T, S, E]) = {
    db.run(
      data.transactionally
    ) onComplete {
      case Success(value) => println(value)
      case Failure(e) => e.printStackTrace()
    }
  }

}

object StatusRepository {

  case class StatusChange(id: String, status: String) {
    val toEntity: StatusEntity = StatusEntity(id, status)

  }

}
