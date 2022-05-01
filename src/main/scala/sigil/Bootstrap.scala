package sigil

import sigil.config.{Config, DBConnection}
import sigil.pub.SupportedPubChannel
import sigil.repo.{FlagRepo, NamespaceRepo, SupportedStorage}
import sigil.service.{EvaluationService, FlagService, NamespaceService}

object Bootstrap {
  final case class Services(
    flagService: FlagService,
    namespaceService: NamespaceService,
    evaluationService: EvaluationService
  )

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
      evalService <- EvaluationService.of(flagService)
    } yield Services(flagService, namespaceService, evalService)
  }

}
