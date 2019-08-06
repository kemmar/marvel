package com.brian.marvel.service

import java.io.{File, FileInputStream, FileOutputStream, InputStream}
import java.net.URL

import akka.NotUsed
import akka.http.scaladsl.model.ContentTypes.`application/octet-stream`
import akka.http.scaladsl.model.{HttpEntity, HttpResponse}
import akka.stream.Materializer
import akka.stream.scaladsl.{Flow, Sink, Source}
import com.brian.marvel.domain.{WuxiaNovel, WuxiaPage}
import nl.siegmann.epublib.domain.{Book, Resource}
import nl.siegmann.epublib.epub.EpubWriter

import scala.concurrent.{ExecutionContext, Future}
import scala.sys.process._

object EpubService {

  private def getResource(path: String): InputStream = new FileInputStream(new File(path))

  private def getResource(path: String, href: String): Resource = new Resource(getResource(path), href)

  def bookFlow(book: Book): Flow[WuxiaPage, File, NotUsed] = Flow.fromFunction[WuxiaPage, File] { page =>

    val dir = new File(s"${book.getTitle}")
    val file = new File(s"${book.getTitle}/${page.title}.htm")
    dir.mkdir()
    file.createNewFile()

    s"echo <html><title>${page.title}</title><body>${page.text}</body></html>" #> file !!

    file.deleteOnExit()
    file
  }

  def createBook(novel: WuxiaNovel)(implicit ec: ExecutionContext, mat: Materializer): Future[HttpResponse] = {
    val book = new Book()

    val coverPic = new File(s"${novel.title}.jpg")

    new URL(s"${novel.image}") #> coverPic !!

    book.setCoverImage(getResource(coverPic.getPath, coverPic.getName))

    val meta = book.getMetadata

    meta.addTitle(novel.title)
    meta.addDescription(novel.description)

    Source
      .fromIterator(() => novel.pages.iterator)
      .via(bookFlow(book))
      .runWith(Sink.seq[File]).map { files =>

      files
        .foreach { f =>
          book.addSection(
            f.getName,
            getResource(f.getAbsolutePath, s"/pages/${f.getName}")
          )

          f.delete()
        }

      val output = new FileOutputStream(s"${novel.title}.epub")
      val epubWriter = new EpubWriter

      epubWriter.write(book, output)

      val file = new File(s"${novel.title}.epub")

      HttpResponse(entity = HttpEntity.fromPath(`application/octet-stream`, file.toPath))

    }


  }

}
