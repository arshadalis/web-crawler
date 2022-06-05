package crawler.core.domain

import crawler.core.domain.Scraper.ScrapedData
import crawler.core.domain.error.CrawlerErrors.{ErrorMessage, InvalidWebPage, ScrapingError}
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers.convertToAnyShouldWrapper

class ScraperSpec extends AnyFlatSpec {
  private val scraperImpl = Scraper.ScraperImpl

  val mediumBlogUrl = "https://medium.com/kodeyoga/variance-is-not-hard-it-is-extremely-useful-f791b09e1f1c"
  val blogDocument = Jsoup.connect(mediumBlogUrl).get()
  private val scrapedData: Either[ScrapingError, ScrapedData] = scraperImpl.scrape(blogDocument)

  "Scraper" should "return return error if the web page is not valid html page" in {
    val document = new Document("dummyUrl")
    scraperImpl.scrape(document) shouldEqual Left(InvalidWebPage(ErrorMessage("No implementation")))
  }

  "Scraper" should "return Scraped data if the web page provided is valid page " in {
    scrapedData.isRight shouldEqual true
  }

  it should "scrape title of the page if present" in {
    val assertionResult = scrapedData.fold(_ => false, scrapedData => scrapedData.title.get.title shouldEqual ("abc"))
    assertionResult shouldEqual true
  }

  it should "scrape Headings from the page" in {
    val assertionResult = scrapedData.fold(_ => false, scrapedData => scrapedData.headings.get.headings.nonEmpty)
    assertionResult shouldEqual true
  }

  it should "scrape Links from the page" in {
    val assertionResult = scrapedData.fold(_ => false, scrapedData => scrapedData.links.get.links.nonEmpty)
    assertionResult shouldEqual true
  }

  it should "scrape Media from the page" in {
    val assertionResult = scrapedData.fold(_ => false, scrapedData => scrapedData.media.get.media.nonEmpty)
    assertionResult shouldEqual true
  }
}
