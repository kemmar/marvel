package com.brian.marvel.controller

import java.awt.image.BufferedImage
import java.io.File
import java.util.UUID
import java.awt.Color

import akka.http.javadsl.server.directives.FileInfo
import akka.stream.scaladsl.Source
import akka.util.ByteString
import com.brian.marvel.db.entity.BinaryEntity
import javax.imageio.ImageIO

import scala.util.Try


class HeightMapGenerator {

  def genHeightMap(width: Int, height: Int): Try[File] = {
    val bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB)

    for {
      x <- (0 until width)
      y <- (0 until height)
    } yield {

      val randomValue = (Math.random * 256).toInt
      val randomColor = new Color(randomValue, randomValue, randomValue)

      bufferedImage
        .setRGB(x, y, randomColor.getRGB)
    }

    val outputFile = new File(s"heightmap-${UUID.randomUUID()}.png")

    Try {
      ImageIO.write(bufferedImage, "png", outputFile)

      outputFile
    }
  }

  def fromFileUpload(metadata: FileInfo, bytesSource: Source[ByteString, Any]): File = {
    bytesSource
      .grouped(64)
      .map { bytes =>
        val grouped = bytes.map(_.head.toInt).grouped(16)

        BinaryEntity(
          0,
          grouped.h
        )
      }
  }
}
