package sigil.repo.impl.pg

import cats.effect.IO
import doobie.util.transactor.Transactor
import sigil.model.{Flag, Variant}
import sigil.repo.FlagRepo
import doobie._
import doobie.implicits._
import doobie.postgres._
import cats.implicits._
import sigil.api.v1.params.{CreateFlagParams, CreateSegmentParams, CreateVariantParams}
import sigil.repo.impl.pg.FlagRepoPGImpl.{FlagRow, VariantRow}

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
  def get(id: Int): IO[Option[Flag]] =
    SQL.selectFlag(id, preload = true).transact(tr)

  def list: IO[Vector[Flag]] =
    SQL
      .list
      .transact(tr)

  def create(params: CreateFlagParams): IO[Option[Flag]] = {
    SQL
      .insertFlag(params)
      .flatMap {
        case Left(_)   => Option.empty[Flag].pure[ConnectionIO]
        case Right(id) => SQL.selectFlag(id, preload = false)
      }
      .transact(tr)
  }

  def createVariant(
    params: CreateVariantParams
  ): IO[Either[String, Variant]] = {
    SQL
      .insertVariant(params)
      .flatMap {
        case Left(err) => Either.left[String, Variant](err).pure[ConnectionIO]
        case Right(id) =>
          Either
            .right[String, Variant](
              Variant(id = id, key = params.key, attachment = params.attachment)
            )
            .pure[ConnectionIO]
      }
      .transact(tr)
  }

  def createSegment(params: CreateSegmentParams) = ???

  def deleteVariant(variantId: Int): IO[Either[String, Int]] =
    sql"""delete from variants where id = $variantId"""
      .update
      .withUniqueGeneratedKeys[Int]("id")
      .map(Either.right[String, Int](_))
      .transact(tr)

  def deleteSegment(segmentId: Int): IO[Either[String, Int]] =
    sql"""delete from segments where id = $segmentId"""
      .update
      .withUniqueGeneratedKeys[Int]("id")
      .map(Either.right[String, Int](_))
      .transact(tr)

  object SQL {
    val list: ConnectionIO[Vector[Flag]] =
      sql"""
           select id, key, description, enabled, notes from flags
         """
        .query[FlagRow]
        .map(_.toFlag)
        .to[Vector]

    def selectFlag(id: Int, preload: Boolean): ConnectionIO[Option[Flag]] = {
      sql"""
           select id, key, description, enabled, notes from flags
           where id = $id
         """
        .query[FlagRow]
        .map(_.toFlag)
        .option
    }

    def selectVariant(id: Int): ConnectionIO[Option[Variant]] = {
      sql"""
        select id, key, attachment from variants
      """
        .query[VariantRow]
        .map(_.toVariant)
        .option
    }

    def insertFlag(params: CreateFlagParams) =
      sql"""
           insert into flags(description, key, namespace_id)
           values (${params.description}, ${params.key}, ${params.namespaceId})
         """
        .update
        .withUniqueGeneratedKeys[Int]("id")
        .attemptSqlState

    def insertVariant(params: CreateVariantParams) =
      sql"""
           insert into variants(flag_id, key, attachment)
           values (${params.flagId}, ${params.key}, ${params.attachment})
         """
        .update
        .withUniqueGeneratedKeys[Int]("id")
        .attemptSomeSqlState {
          case sqlstate.class23.UNIQUE_VIOLATION => "Key must be unique"
        }
  }
}
