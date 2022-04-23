package sigil.service

import sigil.api.v1.params.{CreateFlagParams, CreateSegmentParams, CreateVariantParams}
import sigil.model.{Flag, Segment, Variant}
import sigil.repo.FlagRepo
import zio.Task

import java.util.UUID

trait FlagService {
  def list: Task[Vector[Flag]]
  def get(id: Int): Task[Option[Flag]]
  def create(params: CreateFlagParams): Task[Option[Flag]]

  def flagSegments(flagId: Int): Task[Vector[Segment]]
  def flagVariants(flagId: Int): Task[Vector[Variant]]

  def createVariant(
    params: CreateVariantParams
  ): Task[Either[String, Variant]]
  def createSegment(
    params: CreateSegmentParams
  ): Task[Either[String, Segment]]

  def deleteVariant(flagId: Int, variantId: Int): Task[Either[String, Variant]]
  def deleteSegment(flagId: Int, segmentId: Int): Task[Either[String, Segment]]
}

object FlagService {
  def of(repo: FlagRepo): FlagService = new FlagService {
    def list: Task[Vector[Flag]] = repo.list

    def create(params: CreateFlagParams): Task[Option[Flag]] = params.key match {
      case Some(_) =>
        repo.create(params)
      case None =>
        repo.create(params.copy(key = Some(UUID.randomUUID().toString)))
    }

    def get(id: Int): Task[Option[Flag]] = repo.get(id)

    def flagSegments(flagId: Int): Task[Vector[Segment]] =
      Task.succeed(Vector[Segment]())
    def flagVariants(flagId: Int): Task[Vector[Variant]] =
      Task.succeed(Vector[Variant]())

    def createSegment(params: CreateSegmentParams) = ???
    def createVariant(params: CreateVariantParams) =
      repo.createVariant(params)

    def deleteSegment(flagId: Int, segmentId: Int): Task[Either[String, Segment]] =
      ???
    def deleteVariant(flagId: Int, variantId: Int): Task[Either[String, Variant]] =
      ???
  }
}
