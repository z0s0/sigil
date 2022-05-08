package sigil.repo.impl.pg

import cats.data.{EitherT, OptionT}
import cats.effect.IO
import cats.implicits.{catsSyntaxApplicativeId, catsSyntaxEitherId, catsSyntaxIfM, toFunctorOps}
import doobie.util.transactor.Transactor
import sigil.model.{Flag, Segment, Variant}
import sigil.repo.{DbError, FlagRepo, MutationError, NotFound, ReadError}
import doobie._
import doobie.implicits._
import io.circe.Json
import sigil.api.v1.params.{
  CreateFlagParams,
  CreateSegmentParams,
  CreateVariantParams,
  FindFlagsParams,
  UpdateVariantParams
}
import sigil.repo.impl.pg.FlagRepoPGImpl._
import sigil.repo.DbError.ConnectionIOOps

object FlagRepoPGImpl {
  final case class VariantRow(id: Int, key: String, attachment: Option[String]) {
    def toVariant: Variant =
      Variant(id = id, key = key, attachment = attachment)
  }

  final case class SegmentRow(
    id: Int,
    description: String,
    rank: Int,
    rolloutPpm: Int,
    cId: Int,
    cProperty: String,
    cOperator: String,
    cValue: String
  )

  object SegmentRow {
    def toSegment(segmentRows: Vector[SegmentRow]): Vector[Segment] =
      if (segmentRows.isEmpty) Vector()
      else {
        segmentRows.groupBy(_.id).map {
          case (id, rows) =>
        }
        ???
      }
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

  def flagVariants(flagId: Int): IO[Option[Vector[Variant]]] =
    (for {
      _ <- OptionT(SQL.selectFlag(flagId, preload = false))
      variants <- OptionT.liftF(SQL.selectVariants(flagId))
    } yield variants.map(_.toVariant)).value.transact(tr)

  def flagSegmentIds(flagId: Int): IO[Option[Vector[Int]]] =
    (for {
      _ <- OptionT(sql"select 1 from flags where id = ${flagId}".query.option)
      ids <- OptionT.liftF(
        sql"select id from segments where flag_id = ${flagId} order by rank".query[Int].to[Vector]
      )
    } yield ids).value.transact(tr)

  def flagSegments(flagId: Int): IO[Option[Vector[Segment]]] =
    (for {
      _ <- OptionT(sql"select 1 from flags where id = ${flagId}".query.option)

      segments <- OptionT.liftF {
        sql"""
            select s.id, s.description, s.rank, s.rollout_ppm, c.id, c.property, c.operator, c.value
            from segments s 
            join constraints c on c.segment_id = s.id 
            where flag_id = ${flagId}
          """
          .query[SegmentRow]
          .to[Vector]
          .map(SegmentRow.toSegment)
      }
    } yield segments).value.transact(tr)

  def create(params: CreateFlagParams): IO[Either[MutationError, Flag]] =
    (for {
      id <- EitherT { SQL.insertFlag(params).withMutationErrorsHandling }
      flag <- EitherT.fromOptionF(
        SQL.selectFlag(id, preload = false),
        MutationError.impossible
      )
    } yield flag).transact(tr).value

  def createVariant(
    flagId: Int,
    params: CreateVariantParams
  ): IO[Either[DbError, Variant]] =
    (SQL
      .selectFlag(flagId, preload = false)
      .flatMap {
        case Some(_) =>
          SQL
            .insertVariant(flagId, params)
            .map(id => Variant(id, params.key, params.attachment).asRight[DbError])
        case None =>
          ReadError.notFound(s"flag with id ${flagId} not found").asLeft[Variant].pure[ConnectionIO]
      })
      .transact(tr)

  def updateVariant(variantId: Int, params: UpdateVariantParams): IO[Either[DbError, Variant]] =
    SQL
      .variantExists(variantId)
      .ifM(
        SQL
          .updateVariant(variantId, params)
          .as(Variant(variantId, params.key, params.attachment).asRight[DbError]),
        ReadError
          .notFound(s"variant with id ${variantId} not found")
          .asLeft[Variant]
          .pure[ConnectionIO]
      )
      .transact(tr)

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
           select id, key, description, created_by, updated_by, enabled, snapshot_id, notes, data_records_enabled, entity_type from flags
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

    def selectVariants(flagId: Int): ConnectionIO[Vector[VariantRow]] =
      sql"""
        select id, key, attachment from variants where flag_id = ${flagId}
      """
        .query[VariantRow]
        .to[Vector]

    def insertFlag(params: CreateFlagParams) =
      sql"""
           insert into flags(description, key, namespace_id)
           values (${params.description}, ${params.key}, ${params.namespaceId})
         """
        .update
        .withUniqueGeneratedKeys[Int]("id")

    def insertVariant(flagId: Int, params: CreateVariantParams) =
      sql"""
           insert into variants(flag_id, key, attachment)
           values (${flagId}, ${params.key}, ${params.attachment})
         """
        .update
        .withUniqueGeneratedKeys[Int]("id")

    def variantExists(variantId: Int): ConnectionIO[Boolean] =
      sql"select 1 from variants where id = ${variantId}"
        .query[Int]
        .option
        .map(_.fold(false)(_ => true))

    def updateVariant(variantId: Int, params: UpdateVariantParams) =
      sql"update variants set key = ${params.key}, attachment = ${params.attachment} where id = ${variantId}"
        .update
        .run
  }
}
