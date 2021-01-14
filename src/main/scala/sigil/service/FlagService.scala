package sigil.service

import sigil.api.v1.params.{
  CreateFlagParams,
  CreateSegmentParams,
  CreateVariantParams
}
import sigil.model.{Flag, Segment, Variant}

trait FlagService[F[_]] {
  def list: F[Vector[Flag]]
  def get(id: Int): F[Option[Flag]]
  def create(params: CreateFlagParams): F[Option[Flag]]

  def flagSegments(flagId: Int): F[Vector[Segment]]
  def flagVariants(flagId: Int): F[Vector[Variant]]

  def createVariant(params: CreateVariantParams): F[Either[String, Variant]]
  def createSegment(params: CreateSegmentParams): F[Either[String, Segment]]

  def deleteVariant(flagId: Int, variantId: Int): F[Either[String, Variant]]
  def deleteSegment(flagId: Int, segmentId: Int): F[Either[String, Segment]]
}
