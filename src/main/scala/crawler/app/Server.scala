package crawler.app


import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorSystem, Behavior, PostStop}
import akka.http.scaladsl.Http
import akka.http.scaladsl.Http.ServerBinding
import crawler.repo.interpreter.ConcurrentCrawlingService
import crawler.routes.CrawlerRoutes

import scala.concurrent.Future
import scala.io.StdIn

object Server {

  sealed trait ServerMessage
  private final case class StartFailed(cause: Throwable) extends ServerMessage
  private final case class Started(binding: ServerBinding) extends ServerMessage
  case object Stop extends ServerMessage


  def apply(host:String, port: Int): Behavior[ServerMessage] = Behaviors.setup { context =>

    implicit val system = context.system


    val concurrentCrawlerService = context.spawn(ConcurrentCrawlingService(), "Concurrent-crawler")
    val routes = new CrawlerRoutes(concurrentCrawlerService)

    val serverBinding: Future[Http.ServerBinding] =
      Http().newServerAt(host, port).bind(routes.routes)

    context.pipeToSelf(serverBinding){
      case scala.util.Success(binding) => Started(binding)
      case scala.util.Failure(ex)      => StartFailed(ex)
    }

    def running(binding: ServerBinding): Behavior[ServerMessage] =
      Behaviors.receiveMessagePartial[ServerMessage] {
        case Stop =>
          context.log.info(
            "Stopping server http://{}:{}/",
            binding.localAddress.getHostString,
            binding.localAddress.getPort)
          Behaviors.stopped
      }.receiveSignal {
        case (_, PostStop) =>
          binding.unbind()
          Behaviors.same
      }

    def starting(wasStopped: Boolean): Behaviors.Receive[ServerMessage] =
      Behaviors.receiveMessage[ServerMessage] {
        case StartFailed(cause) =>
          throw new RuntimeException("Server failed to start", cause)
        case Started(binding) =>
          context.log.info(
            "Server online at http://{}:{}/",
            binding.localAddress.getHostString,
            binding.localAddress.getPort)
          if (wasStopped) context.self ! Stop
          running(binding)
        case Stop =>
          // we got a stop message but haven't completed starting yet,
          // we cannot stop until starting has completed
          starting(wasStopped = true)
      }

    starting(wasStopped = false)
  }

  def main(args: Array[String]): Unit = {
    val system: ActorSystem[Server.ServerMessage] = {
      ActorSystem(Server("localhost", 8080), "ConcurrentWebCrawler")

    }
    StdIn.readLine()
  }

}
