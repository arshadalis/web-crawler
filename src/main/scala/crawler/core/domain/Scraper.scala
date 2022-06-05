package crawler.core.domain

import crawler.core.domain.error.CrawlerErrors.{ErrorMessage, InvalidWebPage, ScrapingError}
import org.jsoup.nodes.Document
import org.jsoup.select.Elements

import scala.collection.JavaConverters._
import java.lang
import scala.reflect.internal.Variance.Extractor

object Scraper {

  trait Scraper {
    def scrape(HTMLDocument: Document): Either[ScrapingError, ScrapedData]
  }

  object ScraperImpl extends Scraper {
    override def scrape(document: Document): Either[ScrapingError, ScrapedData] = {

      val mayBeTitle = extractNonEmptyString(document, document => document.title()).map(Title)
      val mayBeBody = extractNonEmptyString(document, document => document.body().data()).map(Body)

      val mayBeHeadings = extractFromNonEmptyList(
        extractElements(document, "h[1-6]"),
        headingTags => Headings(headingTags)
      )

      val mayBeLinks = extractFromNonEmptyList(
        extractElements(document, "a[href]"),
        linksTags => Links(linksTags)
      )

      val mayBeMedia = extractFromNonEmptyList(
        extractElements(document, "[src]"),
        mediaTags => Media(mediaTags)
      )

      Right(ScrapedData(mayBeTitle, mayBeBody, mayBeHeadings, mayBeLinks, mayBeMedia))
    }

    private def extractNonEmptyString[T <: lang.String](document: Document, extractor: Document => T) = {
      val data = extractor(document)
      if (data != "") Some(data) else None
    }

    private def extractFromNonEmptyList[T](list: List[String], func: List[String] => T): Option[T] = list match {
      case Nil => None
      case list => Some(func(list))
    }

    private def extractElements(document: Document, extractorQuery: String) =
      document.select(extractorQuery).eachText().asScala.toList


  }

  case class ScrapedData(
                          title: Option[Title],
                          body: Option[Body],
                          headings: Option[Headings],
                          links: Option[Links],
                          media: Option[Media]
                        )

  case class Title(title: String) extends AnyVal

  case class Body(bodyString: String) extends AnyVal

  case class Headings(headings: List[String]) extends AnyVal

  case class Links(links: List[String]) extends AnyVal

  case class Media(media: List[String]) extends AnyVal


}
