package com.brian.marvel.service

import java.awt.image.BufferedImage
import java.io.File
import java.util.UUID
import java.awt.Color
import java.nio.ByteBuffer

import akka.Done
import akka.http.javadsl.server.directives.FileInfo
import akka.stream.Materializer
import akka.stream.scaladsl.{Sink, Source}
import akka.util.ByteString
import com.brian.marvel.db.entity.BinaryEntity
import javax.imageio.ImageIO

import scala.collection.immutable
import scala.concurrent.Future
import scala.util.Try

class HeightMapGeneratorService()(implicit mat: Materializer) {

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

  def fromFileUpload(metadata: FileInfo, bytesSource: Source[ByteString, Any]): Future[Seq[BinaryEntity]] = {
    bytesSource
      .map { bytes =>
        val byteToInt = ByteBuffer.wrap(bytes.toArray).getInt
        val totalBytes = byteToInt / 255

        val delta = byteToInt - (totalBytes * 255)

        BinaryEntity(
          0,
          totalBytes.abs,
          delta.abs,
          totalBytes < 0
        )
      }.runFold(Seq.empty[BinaryEntity])(_ :+ _)
  }
}
