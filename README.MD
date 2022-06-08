# Concurrent Web crawler (WIP)  
Web crawler to crawl provided list of urls concurrently and scrape basic info like Title, Body, Headings, Links, Media.

## About the solution  
### Algorithm
Crawler can be of multiple types depending on type info that needed to extracted from pages. The Scraper extracts the required  
info from given HTML page. The solution has Crawler having simple scraper to scrape info mentioned above.  
For concurrency `Akka typed` framework is used. For http server `Akka HTTP` is used.     


## Domain Model  
* `Scraper` Type for implementing scraping of specific type. It takes HTML document and returns scraped data.
* `Crawler`  Type for implementing crawling of specific type. It takes Url and returns either Crawled data or crawling error.  
Implementations of above types are in package `crawler.domain.model.core.interpreter`  

* Akka Actors
* `Crawler`  Actor to crawl one url at time. This is the unit of concurrency in the system.  
* `CrawlingService` Parent actor to spawn child actors, which will individually crawl one url.  
* `Server`  Akka http server

#### The solution is implemented using Akka Ask pattern.  
#### Core domain model implemented using TDD practices, have made commits often.  



### Note : Currently it is returning time out exception. The issue is in ConcurrentCrawlingService in below code block  
  ```
  val allCrawledReplies = messageUrlAndActor.urls.map(
      url => {
        val target = context.spawn(CrawlerActor(), s"${url.urlString}-crawler")
        target.ask(ref => Crawl(url, ref))
      }
    ).toList
   ```    
The child actors are spawned using context.spawn method, this method is not thread safe. As we are using future callbacks ask pattern  
the messages are getting lost.  
Work is in progress to fix it using some other approach may be `Aggregator pattern`.