package sigil.service

import sigil.api.v1.params.{
  CreateFlagParams,
  CreateSegmentParams,
  CreateVariantParams,
  FlagsListParams
}
import sigil.model.{Flag, Segment, Variant}
import sigil.repo.FlagRepo.FlagRepo
import sigil.service.impl.FlagServiceImpl
import zio.{Has, Task, ZLayer}

object FlagService {

  type FlagService = Has[Service]

  trait Service {
    def list(params: List[FlagsListParams]): Task[Vector[Flag]]
    def get(id: Int): Task[Option[Flag]]
    def create(params: CreateFlagParams): Task[Option[Flag]]

    def flagSegments(flagId: Int): Task[Vector[Segment]]
    def flagVariants(flagId: Int): Task[Vector[Variant]]

    def createVariant(
      params: CreateVariantParams
    ): Task[Either[String, Variant]]
    def createSegment(
      params: CreateSegmentParams
    ): Task[Either[String, Segment]]

    def deleteVariant(flagId: Int,
                      variantId: Int): Task[Either[String, Variant]]
    def deleteSegment(flagId: Int,
                      segmentId: Int): Task[Either[String, Segment]]
  }

  val live: ZLayer[FlagRepo, Nothing, FlagService] = ZLayer.fromService {
    repo =>
      new FlagServiceImpl(repo)
  }
}
