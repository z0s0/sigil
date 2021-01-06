package sigil

import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorSystem, Behavior}
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Route
import org.slf4j.LoggerFactory
import sigil.api.v1.FlagRoutes
import sigil.repo.impl.pg.FlagRepoPGImpl
import sigil.service.impl.FlagServiceImpl

object RootActor {
  sealed trait Command
  case object Stop extends Command

  def apply(): Behavior[Command] = Behaviors.receiveMessage {
    case Stop => Behaviors.stopped
  }
}

object Main {
  def createRoutes(): Route = {
    val flagRepo = new FlagRepoPGImpl

    val flagService = new FlagServiceImpl(flagRepo)

    new FlagRoutes(flagService).route
  }

  def main(args: Array[String]): Unit = {
    implicit val system = ActorSystem(RootActor(), "sigil")

    val logger = LoggerFactory.getLogger("RuntimeReporter")
    implicit val ctx = system.executionContext

    val binding = Http().newServerAt("localhost", 5000).bind(createRoutes())

    binding.foreach(_ => println("server started at 5000"))
  }
}
