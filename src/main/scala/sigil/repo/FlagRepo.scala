package sigil.repo

import sigil.api.v1.params.{
  CreateFlagParams,
  CreateSegmentParams,
  CreateVariantParams
}
import sigil.model.{Flag, Segment, Variant}

trait FlagRepo[F[_]] {
  def list: F[Vector[Flag]]
  def get(id: Int): F[Option[Flag]]
  def create(params: CreateFlagParams): F[Option[Flag]]

  def createVariant(params: CreateVariantParams): F[Either[String, Variant]]
  def createSegment(params: CreateSegmentParams): F[Either[String, Segment]]

  def deleteVariant(variantId: Int): F[Either[String, Int]]
  def deleteSegment(segmentId: Int): F[Either[String, Int]]
}
