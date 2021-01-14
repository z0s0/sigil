package sigil.repo.impl.pg

import cats.effect.IO
import doobie.util.transactor.Transactor
import sigil.model.Flag
import sigil.repo.FlagRepo
import doobie._
import doobie.implicits._
import cats.implicits._
import sigil.api.v1.params.CreateFlagParams
import sigil.repo.impl.pg.FlagRepoPGImpl.FlagRow

import scala.concurrent.Future

object FlagRepoPGImpl {
  final case class FlagRow(id: Int,
                           key: String,
                           description: String,
                           enabled: Option[Boolean],
                           notes: Option[String]) {
    def toFlag: Flag = Flag(
      id = id,
      key = key,
      description = description,
      enabled = enabled.getOrElse(false),
      notes = notes
    )
  }
}

class FlagRepoPGImpl(tr: Transactor[IO]) extends FlagRepo[Future] {
  override def get(id: Int): Future[Option[Flag]] =
    SQL.select(id).transact(tr).unsafeToFuture()

  def list: Future[Vector[Flag]] =
    SQL.list
      .transact(tr)
      .unsafeToFuture()

  override def create(params: CreateFlagParams): Future[Option[Flag]] = {
    SQL
      .insertFlag(params)
      .flatMap {
        case Some(id) =>
          SQL.select(id)
        case None => Option.empty[Flag].pure[ConnectionIO]
      }
      .transact(tr)
      .unsafeToFuture()
  }

  object SQL {
    def list: ConnectionIO[Vector[Flag]] =
      sql"""
           select id, key, description, enabled, notes from flags
         """
        .query[FlagRow]
        .map(_.toFlag)
        .to[Vector]

    def select(id: Int): ConnectionIO[Option[Flag]] =
      sql"""
           select id, key, description, enabled, notes from flags
           where id = $id
         """
        .query[FlagRow]
        .map(_.toFlag)
        .option

    def insertFlag(params: CreateFlagParams) =
      sql"""
           insert into flags(description, key, namespace_id)
           values (${params.description}, ${params.key}, ${params.namespaceId})
         """.update
        .withGeneratedKeys[Int]("id")
        .compile
        .last
  }
}
