package com.brian.marvel.db

import com.brian.marvel.db.entity.BinaryEntity
import slick.jdbc.MySQLProfile.api._
import slick.lifted.ProvenShape

class BinaryTable[T <: Product](tag: Tag) extends Table[BinaryEntity](tag, "binary") {

  def id = column[Int]("bin_id", O.PrimaryKey, O.AutoInc)

  def firstGroup: Rep[Array[Byte]] = column[Array[Byte]]("first_group")
  def secondGroup: Rep[Array[Byte]] = column[Array[Byte]]("second_group")
  def thirdGroup: Rep[Array[Byte]] = column[Array[Byte]]("third_group")
  def forthGroup: Rep[Array[Byte]] = column[Array[Byte]]("forth_group")

  override def * : ProvenShape[BinaryEntity] =
    (
      id,
      firstGroup,
      secondGroup,
      thirdGroup,
      forthGroup
    ) <> ((BinaryEntity.apply _).tupled, BinaryEntity.unapply)
}