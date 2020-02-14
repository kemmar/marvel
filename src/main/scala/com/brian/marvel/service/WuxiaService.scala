package com.brian.marvel.service

import java.net.URL

import com.brian.marvel.domain.{LinkReference, WuxiaNovel, WuxiaPage}
import org.htmlcleaner.HtmlCleaner

import scala.collection.JavaConverters._
import scala.concurrent.{ExecutionContext, Future, blocking}


object WuxiaService {

  def findPages(link: String): LinkReference = {
    val node = new HtmlCleaner().clean(new URL(link))

    LinkReference {
      node
        .findElementByAttValue(
          "class",
          "main version-chap",
          true,
          true)
        .getAllElements(true)
        .map(_.getAttributes
          .asScala
          .toMap
          .get("href"))
        .toSeq
        .flatten
        .sortBy(_.split("-").last.toInt)
        .take(20)
    }
  }

  private def readLinkToWuxiaPage: String => WuxiaPage = { link: String =>
    println(link)
    val node = new HtmlCleaner().clean(new URL(link))

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

    WuxiaPage(title, text.map(x => s"<p> $x </p>").dropRight(1).mkString, link.split("-").last.toInt)
  }

  def readPageFuture(str: String)(implicit ec: ExecutionContext) = Future {
    blocking {
      readLinkToWuxiaPage(str)
    }
  }

  def buildNovelInformation(homePageUrl: String): WuxiaNovel = {
    val node = new HtmlCleaner().clean(new URL(homePageUrl))

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
      pages = findPages(homePageUrl)
    )

  }

}
