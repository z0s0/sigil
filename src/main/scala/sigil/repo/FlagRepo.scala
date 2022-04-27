package sigil.repo

import cats.effect.IO
import doobie.util.transactor.Transactor
import sigil.api.v1.params.{
  CreateFlagParams,
  CreateSegmentParams,
  CreateVariantParams,
  FindFlagsParams
}
import sigil.model.{Flag, Segment, Variant}
import sigil.repo.impl.pg.FlagRepoPGImpl

trait FlagRepo {
  def list(params: FindFlagsParams): IO[Vector[Flag]]
  def get(id: Int): IO[Option[Flag]]
  def create(params: CreateFlagParams): IO[Either[MutationError, Flag]]

  def createVariant(
    params: CreateVariantParams
  ): IO[Either[MutationError, Variant]]
  def createSegment(
    params: CreateSegmentParams
  ): IO[Either[MutationError, Segment]]

  def deleteVariant(variantId: Int): IO[Either[MutationError, Unit]]
  def deleteSegment(segmentId: Int): IO[Either[MutationError, Unit]]
}

object FlagRepo {
  def of(transactor: Transactor[IO], supportedStorage: SupportedStorage): FlagRepo =
    supportedStorage match {
      case PG => new FlagRepoPGImpl(transactor)
    }
}
