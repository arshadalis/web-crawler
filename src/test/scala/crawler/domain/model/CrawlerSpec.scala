package crawler.domain.model

import crawler.domain.model.core.Url
import crawler.domain.model.core.error.CrawlerErrors.{ErrorMessage, InvalidWebPage}
import crawler.domain.model.core.interpreter.CrawlerImpl
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers.convertToAnyShouldWrapper

class CrawlerSpec extends AnyFlatSpec {

  val crawler = CrawlerImpl

  "Crawler" should "return error if invalid html page link provided for Crawling" in {
    val malformedUrl = Url("http://example.com:-80/")
    val urlNonExisting = Url("https://stackoverflow.com/quest")

    crawler.crawl(malformedUrl) shouldEqual Left(InvalidWebPage(ErrorMessage("Crawling error, Error: Malformed URL: http://example.com:-80/")))
    crawler.crawl(urlNonExisting) shouldEqual Left(InvalidWebPage(ErrorMessage("Crawling error, Error: HTTP error fetching URL. Status=404, URL=[https://stackoverflow.com/quest]")))
  }

  "Crawler" should "return Crawled data if valid html page link is provided" in {
    val url = Url("https://doc.akka.io/docs/akka/current/index.html")

    crawler.crawl(url).isRight shouldEqual true
  }

  it should "contain Crawled data like Title, headings etc" in {
    val mediumBlogUrl = Url("https://medium.com/kodeyoga/variance-is-not-hard-it-is-extremely-useful-f791b09e1f1c")

    val errorOrCrawledData = crawler.crawl(mediumBlogUrl)
    errorOrCrawledData.isRight shouldEqual true
    errorOrCrawledData.right.get.scrapingData.isEmpty() shouldEqual false
  }

}
