package sigil.service

import cats.effect.IO
import sigil.api.v1.params.{
  CreateFlagParams,
  CreateSegmentParams,
  CreateVariantParams,
  FindFlagsParams
}
import sigil.model.{Flag, Segment, Variant}
import sigil.repo.{FlagRepo, MutationError, NotFound}

import java.util.UUID

trait FlagService {
  def list(params: FindFlagsParams): IO[Vector[Flag]]
  def get(id: Int): IO[Option[Flag]]
  def create(params: CreateFlagParams): IO[Either[MutationError, Flag]]

  def flagSegments(flagId: Int): IO[Vector[Segment]]
  def flagVariants(flagId: Int): IO[Option[Vector[Variant]]]

  def createVariant(
    params: CreateVariantParams
  ): IO[Either[MutationError, Variant]]
  def createSegment(
    params: CreateSegmentParams
  ): IO[Either[MutationError, Segment]]

  def deleteVariant(flagId: Int, variantId: Int): IO[Either[MutationError, Unit]]
  def deleteSegment(flagId: Int, segmentId: Int): IO[Either[MutationError, Unit]]
}

object FlagService {
  def of(repo: FlagRepo): FlagService = new FlagService {
    def list(params: FindFlagsParams): IO[Vector[Flag]] = repo.list(params: FindFlagsParams)

    def create(params: CreateFlagParams): IO[Either[MutationError, Flag]] = params.key match {
      case Some(_) => repo.create(params)
      case None    => repo.create(params.copy(key = Some(UUID.randomUUID().toString)))
    }

    def get(id: Int): IO[Option[Flag]] = repo.get(id)

    def flagSegments(flagId: Int): IO[Vector[Segment]] =
      IO.pure(Vector[Segment]())
    def flagVariants(flagId: Int): IO[Option[Vector[Variant]]] =
      repo.flagVariants(flagId)

    def createSegment(params: CreateSegmentParams) = repo.createSegment(params)
    def createVariant(params: CreateVariantParams) = repo.createVariant(params)

    def deleteSegment(flagId: Int, segmentId: Int): IO[Either[MutationError, Unit]] =
      repo.deleteSegment(segmentId)
    def deleteVariant(flagId: Int, variantId: Int): IO[Either[MutationError, Unit]] =
      repo.deleteVariant(variantId)
  }
}
