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

import scala.concurrent.Future

class FlagServiceImpl(flagRepo: FlagRepo[Future]) extends FlagService[Future] {
  override def list: Future[Vector[Flag]] = flagRepo.list

  override def create(params: CreateFlagParams): Future[Option[Flag]] = {
    params.key match {
      case Some(_) =>
        flagRepo.create(params)
      case None =>
        flagRepo.create(params.copy(key = Some(UUID.randomUUID().toString)))
    }
  }

  override def get(id: Int): Future[Option[Flag]] = flagRepo.get(id)

  override def flagSegments(flagId: Int): Future[Vector[Segment]] =
    Future.successful(Vector[Segment]())
  override def flagVariants(flagId: Int): Future[Vector[Variant]] =
    Future.successful(Vector[Variant]())

  override def createSegment(params: CreateSegmentParams) = ???
  override def createVariant(params: CreateVariantParams) =
    flagRepo.createVariant(params)

  override def deleteSegment(flagId: Int,
                             segmentId: Int): Future[Either[String, Segment]] =
    ???
  override def deleteVariant(flagId: Int,
                             variantId: Int): Future[Either[String, Variant]] =
    ???
}
