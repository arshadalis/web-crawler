package crawler.domain.model.core.interpreter

import crawler.domain.model.core.Scraper.{Body, Headings, Links, Media, ScrapedData, Scraper, Title}
import crawler.domain.model.core.error.CrawlerErrors.{ErrorMessage, NoMeaningFulData, ScrapingError}
import org.jsoup.nodes.Document
import scala.jdk.CollectionConverters._

import java.lang

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
      extractElements(document, document => document.select("a[href]").eachAttr("abs:href")),
      linksTags => Links(linksTags)
    )
  }

  private def extractHeadings(document: Document) = {
    extractFromNonEmptyList(
      extractElements(document, document => document.select("h0, h1, h2, h3, h4, h5, h6").eachText()),
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
