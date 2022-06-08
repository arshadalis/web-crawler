package crawler.routes

import akka.actor.typed.{ActorRef, ActorSystem}
import akka.http.scaladsl.server.Directives.{as, complete, entity, onComplete, path, post}
import akka.http.scaladsl.server.Route
import akka.util.Timeout
import crawler.domain.model.core.{RequestUrls, Url}
import crawler.repo.interpreter.ConcurrentCrawlingService
import crawler.repo.interpreter.ConcurrentCrawlingService.StartCrawling

import scala.concurrent.duration.DurationInt

class CrawlerRoutes(val crawlerService : ActorRef[ConcurrentCrawlingService.StartCrawling])(implicit system: ActorSystem[_])  {

  import akka.actor.typed.scaladsl.AskPattern.{Askable, schedulerFromActorSystem}
  import crawler.domain.model.core.UrlFormat._

  implicit val timeout: Timeout = 3.seconds

  val routes : Route =
    path("crawl") {
      post {
        entity(as[RequestUrls]) { requestUrls =>
          val crawlingResult  = crawlerService.ask(ref  =>  StartCrawling(requestUrls.urls.map(Url), ref)).flatten
           onComplete(crawlingResult) { crawlingds =>
             crawlingds.fold(
               error=> complete(s"error got :${error}"),
               crawlingDone => complete(crawlingDone.toString))
          }
        }
      }
    }



}
