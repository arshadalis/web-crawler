package crawler.domain.model.core

import crawler.domain.model.core.error.CrawlerErrors.CrawlingError


trait CrawlingResult {
  val url: Url
}

case class CrawlingFailure(url: Url, result: CrawlingError) extends CrawlingResult

case class CrawlingSuccess(url: Url, result: CrawledData) extends CrawlingResult
