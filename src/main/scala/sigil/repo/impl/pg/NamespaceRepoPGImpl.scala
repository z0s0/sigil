package sigil.repo.impl.pg

import cats.effect.IO
import sigil.api.v1.params.CreateNamespaceParams
import sigil.model.Namespace
import sigil.repo.{DbError, MutationError, NamespaceRepo}
import DbError.ConnectionIOOps
import cats.data.EitherT
import doobie.implicits._
import doobie.ConnectionIO
import doobie.util.transactor.Transactor

final class NamespaceRepoPGImpl(tr: Transactor[IO]) extends NamespaceRepo {

  def list: IO[Vector[Namespace]] = SQL.list.transact(tr)

  def create(
    params: CreateNamespaceParams
  ): IO[Either[MutationError, Namespace]] =
    EitherT(SQL.insert(params))
      .map(id => Namespace(id, params.name))
      .value
      .transact(tr)

  object SQL {
    def select(id: Int): ConnectionIO[Option[Namespace]] =
      sql"""
           select id, name from namespaces 
           where id = $id
         """
        .query[Namespace]
        .option

    def list: ConnectionIO[Vector[Namespace]] =
      sql"""
           select id, name from namespaces
         """
        .query[Namespace]
        .to[Vector]

    def insert(params: CreateNamespaceParams): ConnectionIO[Either[MutationError, Int]] =
      sql"""
           insert into namespaces(name) values(${params.name})
         """
        .update
        .withUniqueGeneratedKeys[Int]("id")
        .withMutationErrorsHandling
  }
}
