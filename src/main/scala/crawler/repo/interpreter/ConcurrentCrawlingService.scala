package crawler.repo.interpreter

import akka.actor.typed.ActorRef
import akka.actor.typed.scaladsl.Behaviors
import akka.util.Timeout
import crawler.domain.model.core.Url
import crawler.repo.interpreter.CrawlerActor.{Crawl, CrawledReply}

import scala.concurrent.Future
import scala.concurrent.duration._
import scala.language.postfixOps

object ConcurrentCrawlingService {

  case class StartCrawling(urls: Set[Url], replyTo: ActorRef[Future[CrawlingDone]])

  case class CrawlingDone(crawledData: List[CrawledReply])

  def apply(): Behaviors.Receive[StartCrawling] = Behaviors.receive { (context, messageUrlAndActor) =>


    implicit val scheduler = context.system.scheduler
    implicit val executionContext = context.executionContext
    import akka.actor.typed.scaladsl.AskPattern._
    implicit val timeout: Timeout = 3 seconds

    context.log.info(s"Got set of urls ${messageUrlAndActor.urls}")

    val allCrawledReplies = messageUrlAndActor.urls.map(
      url => {
        val target = context.spawn(CrawlerActor(), s"${url.urlString}-crawler")
        target.ask(ref => Crawl(url, ref))
      }
    ).toList

    val result = Future.sequence(allCrawledReplies).map(crawledData => CrawlingDone(crawledData))

    messageUrlAndActor.replyTo ! result


    Behaviors.same
  }


}
