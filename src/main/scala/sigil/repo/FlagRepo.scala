package sigil.repo

import cats.effect.IO
import doobie.util.transactor.Transactor
import sigil.api.v1.params.{
  CreateFlagParams,
  CreateVariantParams,
  FindFlagsParams,
  UpdateVariantParams
}
import sigil.model.{Flag, Segment, Variant}
import sigil.repo.impl.pg.FlagRepoPGImpl

trait FlagRepo {
  def list(params: FindFlagsParams): IO[Vector[Flag]]
  def get(id: Int): IO[Option[Flag]]

  def flagVariants(flagId: Int): IO[Option[Vector[Variant]]]
  def flagSegments(flagId: Int): IO[Option[Vector[Segment]]]
  def flagSegmentIds(flagId: Int): IO[Option[Vector[Int]]]

  def create(params: CreateFlagParams): IO[Either[MutationError, Flag]]

  def createVariant(
    flagId: Int,
    params: CreateVariantParams
  ): IO[Either[DbError, Variant]]

  def updateVariant(variantId: Int, params: UpdateVariantParams): IO[Either[DbError, Variant]]

  def deleteVariant(variantId: Int): IO[Either[MutationError, Unit]]
  def deleteSegment(segmentId: Int): IO[Either[MutationError, Unit]]
}

object FlagRepo {
  def of(transactor: Transactor[IO], supportedStorage: SupportedStorage): FlagRepo =
    supportedStorage match {
      case PG => new FlagRepoPGImpl(transactor)
    }
}
