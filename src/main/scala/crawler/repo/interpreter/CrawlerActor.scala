package crawler.repo.interpreter


import akka.actor.typed.ActorRef
import akka.actor.typed.scaladsl.Behaviors
import crawler.domain.model.core.interpreter.CrawlerImpl
import crawler.domain.model.core.{CrawlingResult, CrawlingFailure, CrawlingSuccess, Url}


object CrawlerActor {

  sealed trait CrawlerMessage

  case class Crawl(url: Url, replyTo: ActorRef[CrawledReply]) extends CrawlerMessage

  case class CrawledReply(crawlingResult: CrawlingResult) extends CrawlerMessage

  def apply(): Behaviors.Receive[CrawlerMessage] = Behaviors.receive { (context, message) =>
    message match {
      case Crawl(url, replyTo) => CrawlerImpl.crawl(url) match {
        case Right(crawledData) =>
          context.log.info(s"Crawling for Url=${url.urlString}.")
          replyTo ! CrawledReply(CrawlingSuccess(url, crawledData))

        case Left(crawlingError) => replyTo ! CrawledReply(CrawlingFailure(url, crawlingError))
      }
    }

    Behaviors.same
  }


}
