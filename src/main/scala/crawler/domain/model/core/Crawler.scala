package crawler.domain.model.core

import crawler.domain.model.core.Scraper.{ScrapedData, Scraper}
import crawler.domain.model.core.error.CrawlerErrors.CrawlingError

object Crawler {

  trait Crawler {
    val scraper : Scraper
    def crawl(url: Url): Either[CrawlingError, CrawledData]
  }

}

case class CrawledData(url: Url, scrapingData: ScrapedData)
