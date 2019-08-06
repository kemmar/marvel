package com.brian.marvel.service

import java.net.URL

import com.brian.marvel.domain.{WuxiaNovel, WuxiaPage}
import org.htmlcleaner.HtmlCleaner

import scala.collection.JavaConverters._


object WuxiaService {

  private val cleaner = new HtmlCleaner()

  private def readLinkToWuxiaPage: String => WuxiaPage = { link: String =>
    println(link)

    val node = cleaner.clean(new URL(link))


    val text = node
      .findElementByAttValue(
        "class",
        "reading-content",
        true,
        true).getAllElements(true)
      .filterNot(_.hasAttribute("style"))
      .map(txt => s"${
        txt.getText
          .toString
          .replaceAll("&#8230;", "...")
          .replaceAll("‘", "'")
          .replaceAll("’", "'")
          .replaceAll("&#8220;", "\"")
          .replaceAll("&#8221;", "\"")
          .trim
      }")
      .reduce(_ + _).split("\n")

    val title = text
      .head

    val nextLink: Option[String] = Option(node
      .findElementByAttValue(
        "class",
        "btn next_page",
        true,
        true)).flatMap(_.getAttributes.asScala.toMap.get("href"))

    WuxiaPage(title, text.map(x => s"<p> $x </p>").dropRight(1).mkString, nextLink)
  }

  def streamPages(linkOpt: Option[String], paged: Stream[WuxiaPage] = Stream.empty): Stream[WuxiaPage] = linkOpt match {
    case None => paged
    case Some(link) => {
      val nextPage = readLinkToWuxiaPage(link)
      nextPage #:: streamPages(nextPage.nextLink, paged :+ nextPage)
    }
  }

  def buildNovelInformation(homePageUrl: String) = {
    val node = cleaner.clean(new URL(homePageUrl))

    val title = node
      .findElementByAttValue("property", "og:title", true, true)
      .getAttributeByName("content")

    val description = node
      .findElementByAttValue("id", "editdescription", true, true)
      .getText.toString.trim

    val rootLink = node
      .findElementByAttValue("property", "og:url", true, true)
      .getAttributeByName("content")

    val image = node
      .findElementByAttValue("property", "og:image", true, true)
      .getAttributeByName("content")

    WuxiaNovel(
      title = title,
      description = description,
      image = image,
      pages = streamPages(Some(s"$rootLink/chapter-1")).take(1)
    )

  }

}
