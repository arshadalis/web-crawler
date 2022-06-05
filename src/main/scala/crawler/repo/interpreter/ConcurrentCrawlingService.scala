package crawler.repo.interpreter

import crawler.domain.model.core.error.CrawlerErrors.CrawlingError
import crawler.domain.model.core.{CrawlingResult, Url}
import crawler.repo.CrawlerService

object ConcurrentCrawlingService extends CrawlerService{
  override def crawlFor(urls: List[Url]): Either[CrawlingError, CrawlingResult] = {
    ???
  }
}
