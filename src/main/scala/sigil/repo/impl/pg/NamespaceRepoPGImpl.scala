package sigil.repo.impl.pg

import cats.effect.IO
import sigil.api.v1.params.CreateNamespaceParams
import sigil.model.Namespace
import sigil.repo.NamespaceRepo

import scala.concurrent.Future
import doobie.implicits._
import cats.implicits._
import doobie.free.connection.ConnectionIO
import doobie.util.transactor.Transactor

class NamespaceRepoPGImpl(tr: Transactor[IO]) extends NamespaceRepo[Future] {

  override def list: Future[Vector[Namespace]] =
    SQL.list.transact(tr).unsafeToFuture()

  override def create(
    params: CreateNamespaceParams
  ): Future[Either[String, Namespace]] =
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
      .unsafeToFuture()

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
         """.update
        .withGeneratedKeys[Int]("id")
        .compile
        .last
  }
}
