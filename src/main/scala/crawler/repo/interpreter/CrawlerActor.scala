package crawler.repo.interpreter


import akka.actor.typed.ActorRef
import akka.actor.typed.scaladsl.Behaviors
import crawler.domain.model.core.{CrawlingResult, Failure, Success, Url}
import crawler.domain.model.core.interpreter.CrawlerImpl


object CrawlerActor {

  sealed trait Message

  case class Crawl(url: Url, replyTo: ActorRef[CrawledReply]) extends Message

  case class CrawledReply(crawlingResult: CrawlingResult)

  def apply(): Behaviors.Receive[Crawl] = Behaviors.receiveMessage { message =>
    CrawlerImpl.crawl(message.url) match {
      case Right(crawledData) => message.replyTo ! CrawledReply(Success(message.url, crawledData))
      case Left(crawlingError) => message.replyTo ! CrawledReply(Failure(message.url, crawlingError))
    }
    Behaviors.same
  }


}
