package sigil

import cats.effect.IO
import sigil.api.v1.{Docs, FlagRoutes, NamespaceRoutes}
import sigil.config.{Config, DBConnection}
import sigil.pub.SupportedPubChannel
import sigil.repo.{FlagRepo, NamespaceRepo, SupportedStorage}
import sigil.service.{FlagService, NamespaceService}
import sttp.tapir.server.http4s.Http4sServerInterpreter
import sttp.tapir.swagger.bundle.SwaggerInterpreter

object Bootstrap {
  final case class Services(flagService: FlagService, namespaceService: NamespaceService)

  def of(config: Config) = {
    val (storage, pubChannel) = (
      SupportedStorage.fromString(config.appConfig.persistence),
      SupportedPubChannel.fromString(config.appConfig.pubChannel)
    )

    for {
      _ <- RunMigrations(config.dbConfig)
      transactor = DBConnection.of(config.dbConfig)
      flagRepo = FlagRepo.of(transactor, storage)
      flagService = FlagService.of(flagRepo)
      namespaceRepo = NamespaceRepo.of(transactor, storage)
      namespaceService = NamespaceService.of(namespaceRepo)
    } yield Services(flagService, namespaceService)
  }

}
