package sigil.service.impl

import java.util.UUID

import sigil.api.v1.params.{
  CreateFlagParams,
  CreateSegmentParams,
  CreateVariantParams
}
import sigil.model.{Flag, Segment, Variant}
import sigil.repo.FlagRepo
import sigil.service.FlagService
import zio.Task

class FlagServiceImpl(flagRepo: FlagRepo.Service) extends FlagService.Service {
  override def list: Task[Vector[Flag]] = flagRepo.list

  override def create(params: CreateFlagParams): Task[Option[Flag]] = {
    params.key match {
      case Some(_) =>
        flagRepo.create(params)
      case None =>
        flagRepo.create(params.copy(key = Some(UUID.randomUUID().toString)))
    }
  }

  override def get(id: Int): Task[Option[Flag]] = flagRepo.get(id)

  override def flagSegments(flagId: Int): Task[Vector[Segment]] =
    Task.succeed(Vector[Segment]())
  override def flagVariants(flagId: Int): Task[Vector[Variant]] =
    Task.succeed(Vector[Variant]())

  override def createSegment(params: CreateSegmentParams) = ???
  override def createVariant(params: CreateVariantParams) =
    flagRepo.createVariant(params)

  override def deleteSegment(flagId: Int,
                             segmentId: Int): Task[Either[String, Segment]] =
    ???
  override def deleteVariant(flagId: Int,
                             variantId: Int): Task[Either[String, Variant]] =
    ???
}
