package crawler.domain.model

import crawler.domain.model.core.Scraper
import crawler.domain.model.core.Scraper.ScrapedData
import crawler.domain.model.core.error.CrawlerErrors.{ErrorMessage, NoMeaningFulData, ScrapingError}
import org.jsoup.Jsoup
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers.convertToAnyShouldWrapper
import org.scalatest.{Failed, Succeeded}

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
    val someExpectedHeadings = List("Variance is not hard and it’s extremely useful", "Subtyping", "Generics", "More from kodeyoga")

    val assertionResult = scrapedData.fold(
      _ => false,
      scrapedData => someExpectedHeadings.forall(expected => scrapedData.headings.get.headings.contains(expected)))

    assertionResult shouldEqual true
  }

  it should "scrape Links from the page" in {
    val someExpectedLinks = List("https://policy.medium.com/medium-terms-of-service-9db0094a1e0f")

    val assertionResult = scrapedData.fold(
      _ => Failed,
      scrapedData => someExpectedLinks.forall(expectedLink => scrapedData.links.get.links.contains(expectedLink)) )

    assertionResult shouldEqual true
  }

  it should "scrape Media from the page" in {
    val someExpectedMedia = List("https://miro.medium.com/fit/c/96/96/1*NXV_UAJPkLPBbeg2FBg1hw.jpeg")

    val assertionResult = scrapedData.fold(
      _ => Failed,
      scrapedData => someExpectedMedia.forall(expected => scrapedData.media.get.media.contains(expected))
    )
    assertionResult shouldEqual true
  }
}
