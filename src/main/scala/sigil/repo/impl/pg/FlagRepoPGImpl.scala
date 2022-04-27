package sigil.repo.impl.pg

import cats.data.EitherT
import cats.effect.IO
import doobie.util.transactor.Transactor
import sigil.model.{Flag, Segment, Variant}
import sigil.repo.{FlagRepo, MutationError}
import doobie._
import doobie.implicits._
import cats.implicits._
import sigil.api.v1.params.{
  CreateFlagParams,
  CreateSegmentParams,
  CreateVariantParams,
  FindFlagsParams
}
import sigil.repo.impl.pg.FlagRepoPGImpl._
import sigil.repo.DbError.ConnectionIOOps

object FlagRepoPGImpl {
  final case class VariantRow(id: Int, key: String, attachment: Option[String]) {
    def toVariant: Variant =
      Variant(id = id, key = key, attachment = attachment)
  }

  final case class TagRow()

  sealed trait FlagRow

  // TODO optimize with sql arrays
  def preloadsToFlag(flagRows: Vector[FlagRowWithPreloads]): Option[Flag] =
    if (flagRows.isEmpty) None
    else {
      val firstRow = flagRows.head
      val (variants, segments) = flagRows.foldLeft((Vector.empty[Variant], Vector.empty[Segment])) {
        case (acc, row) =>
          (
            acc._1 :+ Variant(
              id = row.variantId,
              key = row.variantKey,
              attachment = row.variantAttachment
            ),
            acc._2 :+ Segment(
              id = row.segmentId,
              description = row.segmentDescription,
              rank = row.segmentRank,
              rollOut = row.segmentRollout
            )
          )
      }

      Some(
        Flag(
          id = firstRow.id,
          key = firstRow.key,
          description = firstRow.description,
          createdBy = firstRow.createdBy,
          updatedBy = firstRow.updatedBy,
          enabled = firstRow.enabled,
          segments = segments.distinctBy(_.id),
          variants = variants.distinctBy(_.key),
          tags = Vector(),
          snapshotId = firstRow.snapshotId,
          notes = firstRow.notes,
          dataRecordsEnabled = firstRow.dataRecordsEnabled,
          entityType = firstRow.entityType
        )
      )
    }

  final case class PlainFlagRow(
    id: Int,
    key: String,
    description: String,
    createdBy: String,
    updatedBy: String,
    enabled: Boolean,
    snapshotId: Int,
    notes: Option[String],
    dataRecordsEnabled: Boolean,
    entityType: String,
  ) extends FlagRow {
    def toFlag: Flag = Flag(
      id = id,
      key = key,
      description = description,
      createdBy = createdBy,
      updatedBy = updatedBy,
      enabled = enabled,
      segments = Vector(),
      variants = Vector(),
      tags = Vector(),
      snapshotId = snapshotId,
      notes = notes,
      dataRecordsEnabled = dataRecordsEnabled,
      entityType = entityType,
    )
  }

  final case class FlagRowWithPreloads(
    id: Int,
    key: String,
    description: String,
    createdBy: String,
    updatedBy: String,
    enabled: Boolean,
    snapshotId: Int,
    notes: Option[String],
    dataRecordsEnabled: Boolean,
    entityType: String,
    variantId: Int,
    variantKey: String,
    variantAttachment: Option[String],
    segmentId: Int,
    segmentDescription: String,
    segmentRank: Int,
    segmentRollout: Int
  ) extends FlagRow
}

final class FlagRepoPGImpl(tr: Transactor[IO]) extends FlagRepo {
  def get(id: Int): IO[Option[Flag]] = SQL.selectFlag(id, preload = true).transact(tr)

  def list(params: FindFlagsParams): IO[Vector[Flag]] = SQL.list(params).transact(tr)

  def create(params: CreateFlagParams): IO[Either[MutationError, Flag]] =
    (for {
      id <- EitherT { SQL.insertFlag(params).withMutationErrorsHandling }
      flag <- EitherT.fromOptionF(
        SQL.selectFlag(id, preload = false),
        MutationError.impossible
      )
    } yield flag).transact(tr).value

  def createVariant(
    params: CreateVariantParams
  ): IO[Either[MutationError, Variant]] =
    SQL
      .insertVariant(params)
      .map { id =>
        Variant(id = id, key = params.key, attachment = params.attachment).asRight[MutationError]
      }
      .transact(tr)

  def createSegment(params: CreateSegmentParams) = ???

  def deleteVariant(variantId: Int): IO[Either[MutationError, Unit]] =
    sql"""delete from variants where id = $variantId"""
      .update
      .run
      .withMutationErrorsHandling
      .map(_.map(_ => ()))
      .transact(tr)

  def deleteSegment(segmentId: Int): IO[Either[MutationError, Unit]] =
    sql"""delete from segments where id = $segmentId"""
      .update
      .run
      .withMutationErrorsHandling
      .map(_.map(_ => ()))
      .transact(tr)

  object SQL {
    // TODO preload, tags
    def list(params: FindFlagsParams): ConnectionIO[Vector[Flag]] = {
      var sql =
        sql"""select 
          id,
          key,
          description,
          created_by,
          updated_by,
          enabled,
          snapshot_id,
          notes,
          data_records_enabled,
          entity_type
        from flags WHERE 1 = 1 """

      sql = params.enabled.fold(sql)(enabled => sql ++ fr"AND enabled = ${enabled} ")
      sql = (params.description, params.descriptionLike) match {
        case (Some(desc), _)        => sql ++ fr"AND description = ${desc} "
        case (None, Some(descLike)) => sql ++ fr"AND description LIKE ${descLike ++ "%"} "
        case _                      => sql
      }

      sql = params.deleted.fold(sql)(deleted => sql ++ fr"AND deleted = ${deleted} ")
      sql = params.key.fold(sql)(key => sql ++ fr"AND key = ${key} ")

      sql = params.limit.fold(sql)(lim => sql ++ fr"LIMIT ${lim} ")
      sql = params.offset.fold(sql)(offset => sql ++ fr"OFFSET ${offset}")

      sql
        .query[PlainFlagRow]
        .map(_.toFlag)
        .to[Vector]
    }

    def selectFlag(id: Int, preload: Boolean): ConnectionIO[Option[Flag]] = {
      if (preload) {
        sql"""
          select 
            f.id,
            f.key,
            f.description,
            f.created_by,
            f.updated_by,
            f.enabled,
            f.snapshot_id, 
            f.notes, 
            f.data_records_enabled,
            f.entity_type,
            v.id,
            v.key,
            v.attachment,
            s.id,
            s.description,
            s.rank,
            s.rollout_ppm
          FROM flags f 
          JOIN variants v on v.flag_id = f.id  
          JOIN segments s on s.flag_id = f.id
          WHERE f.id = ${id}
        """
          .query[FlagRowWithPreloads]
          .to[Vector]
          .map(preloadsToFlag)
      } else
        sql"""
           select id, key, description, enabled, notes from flags
           where id = $id
         """
          .query[PlainFlagRow]
          .map(_.toFlag)
          .option
    }

    def selectVariant(id: Int): ConnectionIO[Option[Variant]] =
      sql"""
        select id, key, attachment from variants where id = ${id}
      """
        .query[VariantRow]
        .map(_.toVariant)
        .option

    def insertFlag(params: CreateFlagParams) =
      sql"""
           insert into flags(description, key, namespace_id)
           values (${params.description}, ${params.key}, ${params.namespaceId})
         """
        .update
        .withUniqueGeneratedKeys[Int]("id")

    def insertVariant(params: CreateVariantParams) =
      sql"""
           insert into variants(flag_id, key, attachment)
           values (${params.flagId}, ${params.key}, ${params.attachment})
         """
        .update
        .withUniqueGeneratedKeys[Int]("id")
  }
}
