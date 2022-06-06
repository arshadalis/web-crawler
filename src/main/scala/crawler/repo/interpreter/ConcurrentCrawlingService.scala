package crawler.repo.interpreter


import akka.actor.typed.ActorSystem
import akka.util.Timeout
import crawler.domain.model.core.{CrawlingResult, Url}
import crawler.repo.CrawlerService
import crawler.repo.interpreter.CrawlerActor.Crawl

import scala.concurrent.Future
import scala.concurrent.duration._
import scala.language.postfixOps

object ConcurrentCrawlingService extends CrawlerService {

  val actorSystem: ActorSystem[CrawlerActor.Crawl] = ActorSystem(CrawlerActor(), "concurrentWebCrawler")


  override def crawlFor(urls: Set[Url]): Future[ List[CrawlingResult]] = {
    import akka.actor.typed.scaladsl.AskPattern._
    implicit val ec = scala.concurrent.ExecutionContext.global

    implicit val timeout: Timeout = 5 seconds
    implicit val scheduler = actorSystem.scheduler


    Future.sequence(
      urls.map(url => actorSystem.ask(ref => Crawl(url, ref))).toList
    ).map(_.map(a => a.crawlingResult))
  }
}
