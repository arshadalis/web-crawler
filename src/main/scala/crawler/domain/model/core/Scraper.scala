package crawler.domain.model.core

import crawler.domain.model.core.error.CrawlerErrors.ScrapingError
import org.jsoup.nodes.Document

object Scraper {

  trait Scraper {
    def scrape(HTMLDocument: Document): Either[ScrapingError, ScrapedData]
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
