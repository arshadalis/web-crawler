package crawler.domain.model.core.error

object CrawlerErrors {

  case class ErrorMessage(message: String) extends AnyVal

  sealed trait CrawlingError {
    val message: ErrorMessage
  }

  sealed trait ScrapingError extends CrawlingError

  case class InvalidWebPage(message: ErrorMessage) extends CrawlingError

  case class NoMeaningFulData(message: ErrorMessage) extends ScrapingError

}
