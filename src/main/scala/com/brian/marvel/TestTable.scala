package com.brian.marvel

import slick.jdbc.MySQLProfile.api._
import slick.lifted.{Rep, Tag}

class TestTable(tag: Tag) extends Table[StatusEntity](tag: Tag, "status") {

  def statusId: Rep[String] = column[String]("status_id", O.PrimaryKey)
  def status: Rep[String] = column[String]("status", O.Length(16))

  override def * =
    (
      statusId,
      status
    ) <> ((StatusEntity.apply _).tupled, StatusEntity.unapply)
}
