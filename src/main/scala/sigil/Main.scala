package sigil

import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorSystem, Behavior}
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Route
import cats.effect.{Blocker, IO}
import doobie.hikari.HikariTransactor
import doobie.util.ExecutionContexts
import doobie.util.transactor.Transactor
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
  def createRoutes(transactor: Transactor[IO]): Route = {
    val flagRepo = new FlagRepoPGImpl(transactor)

    val flagService = new FlagServiceImpl(flagRepo)

    new FlagRoutes(flagService).route
  }

  def main(args: Array[String]): Unit = {
    implicit val system = ActorSystem(RootActor(), "sigil")
    implicit val cs = IO.contextShift(ExecutionContexts.synchronous)

    val logger = LoggerFactory.getLogger("RuntimeReporter")
    implicit val ctx = system.executionContext

    HikariTransactor
      .newHikariTransactor[IO](
        "org.postgresql.Driver",
        "jdbc:postgresql://localhost:5432/sigil",
        "sigil",
        "harold",
        system.executionContext,
        Blocker.liftExecutionContext(ExecutionContexts.synchronous)
      )
      .use { transactor =>
        val binding =
          Http()
            .newServerAt("localhost", 5000)
            .bind(createRoutes(transactor))

        Migrations.run(
          DbConfig("sigil", "harold", "jdbc:postgresql://localhost:5432/sigil")
        )
        binding.foreach(_ => println("server started at 5000"))
        IO.unit
      }
      .unsafeRunSync()

  }
}
