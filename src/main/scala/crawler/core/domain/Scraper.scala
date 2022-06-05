package crawler.core.domain

import crawler.core.domain.error.CrawlerErrors.{ErrorMessage, NoMeaningFulData, ScrapingError}
import org.jsoup.nodes.Document

import java.lang
import scala.collection.JavaConverters._

object Scraper {

  trait Scraper {
    def scrape(HTMLDocument: Document): Either[ScrapingError, ScrapedData]
  }

  object ScraperImpl extends Scraper {
    override def scrape(document: Document): Either[ScrapingError, ScrapedData] = {

      val mayBeTitle = extractNonEmptyString(document, document => document.title()).map(Title)
      val mayBeBody = extractNonEmptyString(document, document => document.body().data()).map(Body)
      val mayBeHeadings = extractHeadings(document)
      val mayBeLinks = extractLinks(document)
      val mayBeMedia = extractMedia(document)

      val scrapedData = ScrapedData(mayBeTitle, mayBeBody, mayBeHeadings, mayBeLinks, mayBeMedia)

      if (scrapedData.isEmpty())
        Left(NoMeaningFulData(ErrorMessage("No meaningful info like title, headings, links etc. Found.")))
      else Right(scrapedData)
    }

    private def extractMedia(document: Document) = {
      extractFromNonEmptyList(
        extractElements(document, document => document.select("[src]").eachAttr("abs:src")),
        mediaTags => Media(mediaTags)
      )
    }

    private def extractLinks(document: Document) = {
      extractFromNonEmptyList(
        extractElements(document, document => document.select( "a[href]").eachAttr("abs:href")),
        linksTags => Links(linksTags)
      )
    }

    private def extractHeadings(document: Document) = {
      extractFromNonEmptyList(
        extractElements(document,  document => document.select("h0, h1, h2, h3, h4, h5, h6").eachText() ),
        headingTags => Headings(headingTags)
      )
    }

    private def extractNonEmptyString[T <: lang.String](document: Document, extractor: Document => T) = {
      val data = extractor(document)
      if (data != "") Some(data) else None
    }

    private def extractFromNonEmptyList[T](list: List[String], func: List[String] => T): Option[T] = list match {
      case Nil => None
      case list => Some(func(list))
    }

    private def extractElements(document: Document, extractorQuery: Document => java.util.List[String]) = {
      extractorQuery(document).asScala.toList
    }


  }

  case class ScrapedData(
                          title: Option[Title],
                          body: Option[Body],
                          headings: Option[Headings],
                          links: Option[Links],
                          media: Option[Media]
                        ) {
    def isEmpty() = title.isEmpty && body.isEmpty && headings.isEmpty && links.isEmpty && media.isEmpty

  }

  case class Title(title: String) extends AnyVal

  case class Body(bodyString: String) extends AnyVal

  case class Headings(headings: List[String]) extends AnyVal

  case class Links(links: List[String]) extends AnyVal

  case class Media(media: List[String]) extends AnyVal


}
