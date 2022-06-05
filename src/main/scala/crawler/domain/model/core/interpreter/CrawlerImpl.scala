package crawler.domain.model.core.interpreter

import crawler.domain.model.core.Crawler.Crawler
import crawler.domain.model.core.error.CrawlerErrors.{CrawlingError, ErrorMessage, InvalidWebPage}
import crawler.domain.model.core.{CrawledData, Url}
import org.jsoup.Jsoup

import scala.util.Try

object CrawlerImpl extends Crawler {
  val scraper = ScraperImpl

  override def crawl(url: Url): Either[CrawlingError, CrawledData] = for {
    document <- extractDocument(url)
    scrapedData <- scraper.scrape(document)
  } yield CrawledData(url, scrapedData)


  private def extractDocument(url: Url) = {
    Try {
      Jsoup.connect(url.linkString).get()
    }.fold(error => Left(InvalidWebPage(ErrorMessage(s"Crawling error, Error: ${error.getMessage}"))),
      document => Right(document))

  }
}
