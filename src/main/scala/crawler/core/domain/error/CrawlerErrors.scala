package crawler.core.domain.error

object CrawlerErrors {

  case class ErrorMessage(message: String) extends AnyVal

  sealed trait Error {
    val message: ErrorMessage
  }

  sealed trait CrawlingError

  sealed trait ScrapingError

  case class InvalidWebPage(message: ErrorMessage) extends ScrapingError
  case class NoMeaningFulData(message: ErrorMessage) extends ScrapingError

}
