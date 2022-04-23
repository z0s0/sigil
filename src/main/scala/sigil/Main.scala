package sigil

import cats.effect.unsafe.implicits.global
import cats.effect.{ExitCode, IO, IOApp, Resource, ResourceApp}
import org.http4s.blaze.server.BlazeServerBuilder
import org.http4s.server.Router
import sigil.api.v1.FlagRoutes
import sigil.config.{Config, DBConnection}
import sigil.repo.FlagRepo
import sigil.service.FlagService

object Main {
  def main(args: Array[String]): Unit = {
    val server = for {
      config <- Config.load
      transactor = DBConnection.of(config.dbConfig)
      repo = FlagRepo.of(transactor)
      service = FlagService.of(repo)
      routes = FlagRoutes.of(service)
      router = Router("/" -> routes).orNotFound
      server <- BlazeServerBuilder[IO]
        .bindHttp(config.apiConfig.port)
        .withHttpApp(router)
        .resource
        .use(_ => IO.never)
        .as(ExitCode.Success)
    } yield ()

    server.unsafeRunSync()
  }
}
