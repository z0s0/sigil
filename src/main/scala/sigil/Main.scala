package sigil

import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorSystem, Behavior}
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Route
import cats.effect.{Blocker, ContextShift, IO, Resource}
import doobie.hikari.HikariTransactor
import doobie.util.ExecutionContexts
import doobie.util.transactor.Transactor
import cats.implicits._
import sigil.api.v1.flag.VariantRoutes
import sigil.api.v1.{FlagRoutes, NamespaceRoutes}
import sigil.repo.impl.pg.{FlagRepoPGImpl, NamespaceRepoPGImpl}
import sigil.service.impl.{FlagServiceImpl, NamespaceServiceImpl}

import scala.io.StdIn

object RootActor {
  sealed trait Command
  case object Stop extends Command

  def apply(): Behavior[Command] = Behaviors.receiveMessage {
    case Stop => Behaviors.stopped
  }
}

object Main {
  def createRoutes(transactor: Transactor[IO]): Route = {
    import akka.http.scaladsl.server.Directives._

    val flagRepo = new FlagRepoPGImpl(transactor)
    val namespaceRepo = new NamespaceRepoPGImpl(transactor)

    val flagService = new FlagServiceImpl(flagRepo)
    val namespaceService = new NamespaceServiceImpl(namespaceRepo)

    (new VariantRoutes(flagService)).route ~
      new FlagRoutes(flagService).route ~
      (new NamespaceRoutes(namespaceService).route)
  }

  def main(args: Array[String]): Unit = {
    implicit val cs: ContextShift[IO] =
      IO.contextShift(ExecutionContexts.synchronous)

    def makeBinding(tr: Transactor[IO])(implicit system: ActorSystem[_]) = {

      Resource
        .make(
          IO.fromFuture(
            IO(
              Http()(system)
                .newServerAt("localhost", 8080)
                .bind(createRoutes(tr))
            )
          )
        )(b => IO.fromFuture(IO(b.unbind())).map(_ => ()))

    }

    val binding =
      for {
        ce <- ExecutionContexts.fixedThreadPool[IO](32) // our connect EC
        be <- Blocker[IO] // our blocking EC
        xa <- HikariTransactor.newHikariTransactor[IO](
          "org.postgresql.Driver", // driver classname
          "jdbc:postgresql://localhost:5432/sigil", // connect URL
          "sigil", // username
          "harold", // password
          ce, // await connection here
          be // execute JDBC operations here
        )
        sys <- Resource.make(IO(ActorSystem(RootActor(), "sys")))(
          s => IO(s.terminate()).map(_ => ())
        )
        binding <- makeBinding(xa)(sys)
      } yield binding

    val app =
      binding
        .use { binding =>
          for {
            _ <- IO(println(s"Binding on ${binding.localAddress}"))
            _ <- IO(StdIn.readLine())
          } yield ()
        }

    val init = IO(
      Migrations.run(
        DbConfig("sigil", "harold", "jdbc:postgresql://localhost:5432/sigil")
      )
    )

    (init *> app).unsafeRunSync()

  }
}
