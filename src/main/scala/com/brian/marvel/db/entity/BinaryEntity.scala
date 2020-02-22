package com.brian.marvel.db.entity

case class BinaryEntity(
                       id: Int,
                       firstGroup: Array[Byte],
                       secondGroup: Array[Byte],
                       thirdGroup: Array[Byte],
                       forthGroup: Array[Byte]
                       )
