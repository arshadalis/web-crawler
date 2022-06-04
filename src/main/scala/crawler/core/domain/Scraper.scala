package crawler.core.domain

import crawler.core.domain.error.CrawlerErrors.{ErrorMessage, InvalidWebPage, ScrapingError}
import org.jsoup.nodes.Document

object Scraper {

  trait Scraper {
    def scrape(HTMLDocument: Document): Either[ScrapingError, ScrapedData]
  }

  object ScraperImpl extends Scraper{
    override def scrape(HTMLDocument: Document): Either[ScrapingError, ScrapedData] =
      Left(InvalidWebPage(ErrorMessage("No implementation")))
  }

  case class ScrapedData(
                          title: Option[Title],
                          body: Option[Title],
                          headings: Option[Title],
                          links: Option[Title],
                          media: Option[Title],
                        )

  case class Title(title: String) extends AnyVal

  case class Body(bodyString: String) extends AnyVal

  case class Headings(headings: List[String]) extends AnyVal

  case class Links(links: List[String]) extends AnyVal

  case class Media(media: List[String]) extends AnyVal


}
