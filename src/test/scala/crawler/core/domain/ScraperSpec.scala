package crawler.core.domain

import crawler.core.domain.Scraper.ScrapedData
import crawler.core.domain.error.CrawlerErrors.{ErrorMessage, InvalidWebPage, NoMeaningFulData, ScrapingError}
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.scalatest.{Failed, Succeeded}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers.convertToAnyShouldWrapper

class ScraperSpec extends AnyFlatSpec {
  private val scraperImpl = Scraper.ScraperImpl

  val mediumBlogUrl = "https://medium.com/kodeyoga/variance-is-not-hard-it-is-extremely-useful-f791b09e1f1c"
  val blogDocument = Jsoup.connect(mediumBlogUrl).get()
  private val scrapedData: Either[ScrapingError, ScrapedData] = scraperImpl.scrape(blogDocument)

  "Scraper" should "return return error if the web page is not valid html page" in {
    val blankHtml = "<html><head><title></title></head>" + "<body></body></html>";
    val blankDocument = Jsoup.parse(blankHtml)
    scraperImpl.scrape(blankDocument) shouldEqual Left(NoMeaningFulData(ErrorMessage("No meaningful info like title, headings, links etc. Found.")))
  }

  "Scraper" should "return Scraped data if the web page provided is valid page " in {
    scrapedData.isRight shouldEqual true
  }

  it should "scrape title of the page if present" in {
    val expectedTitle = "Variance is not hard and it’s extremely useful | by arshad ali sayed | kodeyoga | Medium"
    val assertionResult = scrapedData.fold(_ => Failed, scrapedData => scrapedData.title.get.title shouldEqual expectedTitle)
    assertionResult shouldEqual Succeeded
  }

  it should "scrape Headings from the page" in {
    val assertionResult = scrapedData.fold(_ => Failed, scrapedData => scrapedData.headings.get.headings.nonEmpty)
    assertionResult shouldEqual true
  }

  it should "scrape Links from the page" in {
    val assertionResult = scrapedData.fold(_ => Failed, scrapedData => scrapedData.links.get.links.nonEmpty)
    assertionResult shouldEqual true
  }

  it should "scrape Media from the page" in {
    val assertionResult = scrapedData.fold(_ => Failed, scrapedData => scrapedData.media.get.media.nonEmpty)
    assertionResult shouldEqual true
  }
}
