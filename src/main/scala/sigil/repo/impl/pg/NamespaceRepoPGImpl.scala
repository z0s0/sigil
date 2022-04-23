package sigil.repo.impl.pg

import cats.effect.IO
import sigil.api.v1.params.CreateNamespaceParams
import sigil.model.Namespace
import sigil.repo.NamespaceRepo
import doobie.implicits._
import cats.implicits._
import doobie.free.connection.ConnectionIO
import doobie.util.transactor.Transactor

final class NamespaceRepoPGImpl(tr: Transactor[IO]) extends NamespaceRepo {

  def list: IO[Vector[Namespace]] =
    SQL.list.transact(tr)

  def create(
    params: CreateNamespaceParams
  ): IO[Either[String, Namespace]] =
    SQL
      .insert(params)
      .map {
        case Some(id) =>
          Either.right[String, Namespace](
            Namespace(id = id, name = params.name)
          )
        case None =>
          Either.left[String, Namespace]("Insertion error")
      }
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

    def insert(params: CreateNamespaceParams): ConnectionIO[Option[Int]] =
      sql"""
           insert into namespaces(name) values(${params.name})
         """
        .update
        .withGeneratedKeys[Int]("id")
        .compile
        .last
  }
}
