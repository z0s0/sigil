package sigil

import sigil.config.{Config, DBConnection}
import sigil.pub.SupportedPubChannel
import sigil.repo.{FlagRepo, NamespaceRepo, SegmentRepo, SupportedStorage}
import sigil.service.{EvaluationService, FlagService, NamespaceService, SegmentsService}

object Bootstrap {
  final case class Services(
    flagService: FlagService,
    namespaceService: NamespaceService,
    evaluationService: EvaluationService,
    segmentsService: SegmentsService
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
      segmentRepo = SegmentRepo.of(transactor, storage)
      namespaceRepo = NamespaceRepo.of(transactor, storage)

      flagService = FlagService.of(flagRepo)
      namespaceService = NamespaceService.of(namespaceRepo)
      segmentsService = SegmentsService.of(flagRepo, segmentRepo)
      evalService <- EvaluationService.of(flagService)
    } yield Services(flagService, namespaceService, evalService, segmentsService)
  }

}
