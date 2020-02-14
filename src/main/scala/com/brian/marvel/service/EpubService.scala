package com.brian.marvel.service

import java.io.{File, FileInputStream, FileOutputStream}
import java.net.URL

import akka.NotUsed
import akka.http.scaladsl.model.ContentTypes.`application/octet-stream`
import akka.http.scaladsl.model.{HttpEntity, HttpResponse}
import akka.stream.Materializer
import akka.stream.scaladsl.{Flow, Source}
import com.brian.marvel.domain.{WuxiaNovel, WuxiaPage}
import nl.siegmann.epublib.domain.{Book, Resource}
import nl.siegmann.epublib.epub.EpubWriter
import nl.siegmann.epublib.service.MediatypeService
import nl.siegmann.epublib.util.IOUtil

import scala.concurrent.{ExecutionContext, Future}
import scala.sys.process._

object EpubService {

  private def getResource(path: String): Array[Byte] =
    IOUtil.toByteArray(new FileInputStream(new File(path)))

  private def getResource(page: String, path: String, href: String): Resource =
    new Resource(page, getResource(path), href, MediatypeService.determineMediaType(href))

  def bookFlow(book: Book): Flow[WuxiaPage, (File, Int), NotUsed] =
    Flow.fromFunction[WuxiaPage, (File, Int)] { page =>
      val dir = new File(s"${book.getTitle}")
      val file = new File(s"${book.getTitle}/${page.title}.htm")

      if (!dir.exists()) {
        dir.mkdir()
      }

      dir.deleteOnExit()
      if (!file.exists()) {
        file.createNewFile()
      }

      s"echo <html><title>${page.title}</title><body>${page.text}</body></html>" #> file !!

      file.deleteOnExit()
      (file, page.page)
    }

  def createBook(novel: WuxiaNovel)(implicit ec: ExecutionContext,
                                    mat: Materializer): Future[HttpResponse] = {
    val book = new Book()

    val coverPic = new File(s"${novel.title}.jpg")

    new URL(s"${novel.image}") #> coverPic !!

    val pic = getResource(null, coverPic.getPath, coverPic.getName)

    book.setCoverImage(pic)

    val meta = book.getMetadata

    meta.addTitle(novel.title)
    meta.addDescription(novel.description)

    Source
      .fromIterator(() => novel.pages.href.iterator)
      .mapAsync(8)(WuxiaService.readPageFuture(_))
      .via(bookFlow(book))
      .runFold(Seq.empty[(File, Int)])(_ :+ _)
      .map { files =>

        files
          .sortBy(_._2)
          .map {
            case (f, page) =>

              book.addSection(
                f.getName,
                getResource(page.toString, f.getAbsolutePath, s"/pages/${f.getName}")
              )
          }

      } map { _ =>
      val output = new FileOutputStream(s"${novel.title}.epub")
      val epubWriter = new EpubWriter

      epubWriter.write(book, output)
      println("finished writing book")

      val file = new File(s"${novel.title}.epub")

      HttpResponse(
        entity = HttpEntity.fromPath(`application/octet-stream`, file.toPath))
    }
  }

}
