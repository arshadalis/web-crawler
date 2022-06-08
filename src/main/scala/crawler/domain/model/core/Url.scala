package crawler.domain.model.core

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import spray.json.DefaultJsonProtocol._

case class Url(urlString: String) extends AnyVal

case class RequestUrls(urls:Set[String]) extends AnyVal


object UrlFormat extends SprayJsonSupport {

  implicit val urlFormat = jsonFormat(Url, "urlString")
  implicit val urlsFormat = jsonFormat(RequestUrls, "urls")




}
