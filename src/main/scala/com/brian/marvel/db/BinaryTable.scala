package com.brian.marvel.db

import com.brian.marvel.db.entity.BinaryEntity
import slick.jdbc.MySQLProfile.api._
import slick.lifted.ProvenShape

class BinaryTable[T <: Product](tag: Tag) extends Table[BinaryEntity](tag, "binary") {

  def id = column[Int]("bin_id", O.PrimaryKey, O.AutoInc)

  def byteX: Rep[Int] = column[Int]("byte_x")
  def byteB: Rep[Int] = column[Int]("byte_d")
  def isPositive: Rep[Boolean] = column[Boolean]("is_positive")

  override def * : ProvenShape[BinaryEntity] =
    (
      id,
      byteX,
      byteB,
      isPositive
    ) <> ((BinaryEntity.apply _).tupled, BinaryEntity.unapply)
}