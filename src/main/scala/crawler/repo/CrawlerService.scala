package crawler.repo

import crawler.domain.model.core.{CrawlingResult, Url}
import crawler.domain.model.core.error.CrawlerErrors.CrawlingError

trait CrawlerService {
  def crawlFor(urls: List[Url]): Either[CrawlingError, CrawlingResult]
}
