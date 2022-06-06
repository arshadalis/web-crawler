package crawler.repo


import crawler.domain.model.core.{CrawlingResult, Url}

import scala.concurrent.Future

trait CrawlerService {

  def crawlFor(urls: Set[Url]): Future[ List[CrawlingResult]]
}
