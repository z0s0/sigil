package sigil.repo.impl.pg

import cats.data.{EitherT, OptionT}
import cats.effect.IO
import doobie.util.transactor.Transactor
import sigil.model.{Flag, Variant}
import sigil.repo.{DbError, FlagRepo, Impossible, MutationError, NotFound, UniquenessViolation}
import doobie._
import doobie.implicits._
import cats.implicits._
import sigil.api.v1.params.{
  CreateFlagParams,
  CreateSegmentParams,
  CreateVariantParams,
  FindFlagsParams
}
import sigil.repo.impl.pg.FlagRepoPGImpl.{FlagRow, VariantRow}
import sigil.repo.DbError.ConnectionIOOps

object FlagRepoPGImpl {
  final case class VariantRow(id: Int, key: String, attachment: Option[String]) {
    def toVariant: Variant =
      Variant(id = id, key = key, attachment = attachment)
  }

  final case class FlagWithPreloadsRow(
    fId: Int,
    fKey: String,
    fDesc: String,
    fEnabled: Option[Boolean],
    fNotes: Option[String],
    variants: Vector[VariantRow],
  )
  final case class FlagRow(
    id: Int,
    key: String,
    description: String,
    enabled: Option[Boolean],
    notes: Option[String]
  ) {
    def toFlag: Flag = Flag(
      id = id,
      key = key,
      description = description,
      enabled = enabled.getOrElse(false),
      notes = notes
    )
  }
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
      var sql = sql"select id, key, description, enabled, notes from flags WHERE 1 = 1 "

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
        .query[FlagRow]
        .map { a =>
          println(a)
          a
        }
        .map(_.toFlag)
        .to[Vector]
    }

    def selectFlag(id: Int, preload: Boolean): ConnectionIO[Option[Flag]] =
      sql"""
           select id, key, description, enabled, notes from flags
           where id = $id
         """
        .query[FlagRow]
        .map(_.toFlag)
        .option

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
