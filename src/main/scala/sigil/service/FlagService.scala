package sigil.service

import cats.data.EitherT
import cats.effect.IO
import sigil.api.v1.params.{
  CreateFlagParams,
  CreateVariantParams,
  FindFlagsParams,
  UpdateVariantParams
}
import sigil.cache.Cache
import sigil.model.{Flag, Segment, Variant}
import sigil.repo.{DbError, FlagRepo, MutationError}

import java.util.UUID

trait FlagService {
  def list(params: FindFlagsParams): IO[Vector[Flag]]
  def get(id: Int): IO[Option[Flag]]
  def create(params: CreateFlagParams): IO[Either[MutationError, Flag]]

  def flagSegments(flagId: Int): IO[Option[Vector[Segment]]]
  def flagVariants(flagId: Int): IO[Option[Vector[Variant]]]

  def createVariant(
    flagId: Int,
    params: CreateVariantParams
  ): IO[Either[DbError, Variant]]

  def updateVariant(
    variantId: Int,
    params: UpdateVariantParams
  ): IO[Either[DbError, Variant]]

  def deleteVariant(variantId: Int): IO[Either[MutationError, Unit]]
  def deleteSegment(segmentId: Int): IO[Either[MutationError, Unit]]
}

object FlagService {
  def of(repo: FlagRepo, cache: Cache): FlagService = new FlagService {
    def list(params: FindFlagsParams): IO[Vector[Flag]] = repo.list(params: FindFlagsParams)

    def create(params: CreateFlagParams): IO[Either[MutationError, Flag]] = params.key match {
      case None =>
        repo.create(params.copy(key = Some(UUID.randomUUID().toString)))

      case Some(_) =>
        (for {
          flag <- EitherT(repo.create(params))
          _ <- EitherT.liftF[IO, MutationError, Unit](IO(cache.putFlag(flag)))
        } yield flag).value

    }

    def get(id: Int): IO[Option[Flag]] = repo.get(id)

    def flagSegments(flagId: Int): IO[Option[Vector[Segment]]] =
      repo.flagSegments(flagId)
    def flagVariants(flagId: Int): IO[Option[Vector[Variant]]] =
      repo.flagVariants(flagId)

    def createVariant(flagId: Int, params: CreateVariantParams): IO[Either[DbError, Variant]] =
      repo.createVariant(flagId, params)

    def updateVariant(variantId: Int, params: UpdateVariantParams): IO[Either[DbError, Variant]] =
      repo.updateVariant(variantId, params)

    def deleteSegment(segmentId: Int): IO[Either[MutationError, Unit]] =
      repo.deleteSegment(segmentId)
    def deleteVariant(variantId: Int): IO[Either[MutationError, Unit]] =
      repo.deleteVariant(variantId)
  }
}
