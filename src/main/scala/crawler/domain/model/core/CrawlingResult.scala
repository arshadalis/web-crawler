package crawler.domain.model.core

import crawler.domain.model.core.error.CrawlerErrors.CrawlingError


trait CrawlingResult {
  val url: Url
  val isSuccess : Boolean
}

case class Failure(url: Url, result: CrawlingError) extends CrawlingResult {
  override val isSuccess: Boolean = false
}

case class Success(url: Url, result: CrawledData) extends CrawlingResult {
  override val isSuccess: Boolean = true
}
