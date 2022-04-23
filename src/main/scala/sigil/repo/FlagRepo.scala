package sigil.repo

import cats.effect.IO
import doobie.util.transactor.Transactor
import sigil.api.v1.params.{CreateFlagParams, CreateSegmentParams, CreateVariantParams}
import sigil.model.{Flag, Segment, Variant}
import sigil.repo.impl.pg.FlagRepoPGImpl

trait FlagRepo {
  def list: IO[Vector[Flag]]
  def get(id: Int): IO[Option[Flag]]
  def create(params: CreateFlagParams): IO[Option[Flag]]

  def createVariant(
    params: CreateVariantParams
  ): IO[Either[String, Variant]]
  def createSegment(
    params: CreateSegmentParams
  ): IO[Either[String, Segment]]

  def deleteVariant(variantId: Int): IO[Either[String, Int]]
  def deleteSegment(segmentId: Int): IO[Either[String, Int]]
}

object FlagRepo {
  def of(transactor: Transactor[IO]) = new FlagRepoPGImpl(transactor)
}
