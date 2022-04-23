package sigil.repo

import doobie.util.transactor.Transactor
import sigil.api.v1.params.{CreateFlagParams, CreateSegmentParams, CreateVariantParams}
import sigil.model.{Flag, Segment, Variant}
import sigil.repo.impl.pg.FlagRepoPGImpl
import zio.Task

trait FlagRepo {
  def list: Task[Vector[Flag]]
  def get(id: Int): Task[Option[Flag]]
  def create(params: CreateFlagParams): Task[Option[Flag]]

  def createVariant(
    params: CreateVariantParams
  ): Task[Either[String, Variant]]
  def createSegment(
    params: CreateSegmentParams
  ): Task[Either[String, Segment]]

  def deleteVariant(variantId: Int): Task[Either[String, Int]]
  def deleteSegment(segmentId: Int): Task[Either[String, Int]]
}

object FlagRepo {
  def of(transactor: Transactor[Task]) = new FlagRepoPGImpl(transactor)
}
