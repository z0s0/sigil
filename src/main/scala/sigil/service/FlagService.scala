package sigil.service

import cats.effect.IO
import sigil.api.v1.params.{CreateFlagParams, CreateSegmentParams, CreateVariantParams}
import sigil.model.{Flag, Segment, Variant}
import sigil.repo.FlagRepo

import java.util.UUID

trait FlagService {
  def list: IO[Vector[Flag]]
  def get(id: Int): IO[Option[Flag]]
  def create(params: CreateFlagParams): IO[Option[Flag]]

  def flagSegments(flagId: Int): IO[Vector[Segment]]
  def flagVariants(flagId: Int): IO[Vector[Variant]]

  def createVariant(
    params: CreateVariantParams
  ): IO[Either[String, Variant]]
  def createSegment(
    params: CreateSegmentParams
  ): IO[Either[String, Segment]]

  def deleteVariant(flagId: Int, variantId: Int): IO[Either[String, Variant]]
  def deleteSegment(flagId: Int, segmentId: Int): IO[Either[String, Segment]]
}

object FlagService {
  def of(repo: FlagRepo): FlagService = new FlagService {
    def list: IO[Vector[Flag]] = repo.list

    def create(params: CreateFlagParams): IO[Option[Flag]] = params.key match {
      case Some(_) =>
        repo.create(params)
      case None =>
        repo.create(params.copy(key = Some(UUID.randomUUID().toString)))
    }

    def get(id: Int): IO[Option[Flag]] = repo.get(id)

    def flagSegments(flagId: Int): IO[Vector[Segment]] =
      IO.pure(Vector[Segment]())
    def flagVariants(flagId: Int): IO[Vector[Variant]] =
      IO.pure(Vector[Variant]())

    def createSegment(params: CreateSegmentParams) = ???
    def createVariant(params: CreateVariantParams) =
      repo.createVariant(params)

    def deleteSegment(flagId: Int, segmentId: Int): IO[Either[String, Segment]] =
      ???
    def deleteVariant(flagId: Int, variantId: Int): IO[Either[String, Variant]] =
      ???
  }
}
