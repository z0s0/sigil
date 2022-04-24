package sigil

import cats.effect.unsafe.implicits.global
import cats.effect.{ExitCode, IO}
import cats.implicits.toSemigroupKOps
import org.http4s.blaze.server.BlazeServerBuilder
import org.http4s.server.Router
import sigil.api.v1.{FlagRoutes, NamespaceRoutes}
import sigil.config.{Config, DBConnection}
import sigil.pub.SupportedPubChannel
import sigil.repo.{FlagRepo, NamespaceRepo, SupportedStorage}
import sigil.service.{FlagService, NamespaceService}
import org.http4s.syntax.kleisli._

object Main {
  def main(args: Array[String]): Unit = {
    val server = for {
      config <- Config.load
      (storage, pubChannel) = (
        SupportedStorage.fromString(config.appConfig.persistence),
        SupportedPubChannel.fromString(config.appConfig.pubChannel)
      )
      _ <- RunMigrations(config.dbConfig)
      transactor = DBConnection.of(config.dbConfig)

      flagRepo = FlagRepo.of(transactor, storage)
      flagService = FlagService.of(flagRepo)
      namespaceRepo = NamespaceRepo.of(transactor, storage)
      namespaceService = NamespaceService.of(namespaceRepo)

      routes = FlagRoutes.of(flagService) <+> NamespaceRoutes.of(namespaceService)
      router = Router("/" -> routes).orNotFound
      _ <- BlazeServerBuilder[IO]
        .bindHttp(config.apiConfig.port)
        .withHttpApp(router)
        .resource
        .use(_ => IO.never)
        .as(ExitCode.Success)
    } yield ()

    server.unsafeRunSync()
  }
}
