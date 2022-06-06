package crawler.repo

import crawler.domain.model.core.{Failure, Success, Url}
import crawler.repo.interpreter.ConcurrentCrawlingService
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers.convertToAnyShouldWrapper

import scala.concurrent.Await
import scala.concurrent.duration.Duration

class CrawlingServiceSpec extends AnyFlatSpec {

  val concurrentCrawlerService = ConcurrentCrawlingService

  val urls = Set(Url("https://www.google.com"), Url("https://www.reactivemanifesto.org/"))
  val errorUrls = Set(Url("https://www.google.com"), Url("https://www.reactivemanifesto.org/"), Url("https://stackoverflow.com/quest"))
  "Concurrent Crawler" should "return crawling results" in {
    Await.result( concurrentCrawlerService.crawlFor(urls), Duration.Inf).nonEmpty shouldEqual true
  }

  it should "return errors for invalid urls and results for valid urls" in {
    val list = Await.result( concurrentCrawlerService.crawlFor(errorUrls), Duration.Inf)

    val successCrawling = list.foldRight(List.empty[Success])((result,acc) => result match {
      case s@Success(_, _) => acc :+ s
      case _ => acc
    })

    val failureCrawling = list.foldRight(List.empty[Failure])((result,acc) => result match {
      case f@Failure(_, _) => acc :+ f
      case _ => acc
    })


    successCrawling.map(_.result).map(_.scrapingData.title.get.title).contains("Google") shouldEqual true
    successCrawling.map(_.result).map(_.scrapingData.title.get.title).contains("The Reactive Manifesto") shouldEqual true

    failureCrawling.map(_.result).map(_.message.message).contains("Crawling error, Error: HTTP error fetching URL. Status=404, URL=[https://stackoverflow.com/quest]") shouldEqual true





  }

}
