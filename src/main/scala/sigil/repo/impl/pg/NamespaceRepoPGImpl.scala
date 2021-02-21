package sigil.repo.impl.pg

import sigil.api.v1.params.CreateNamespaceParams
import sigil.model.Namespace
import sigil.repo.NamespaceRepo

import doobie.implicits._
import cats.implicits._
import doobie.free.connection.ConnectionIO
import doobie.util.transactor.Transactor
import zio.Task
import zio.interop.catz._

class NamespaceRepoPGImpl(tr: Transactor[Task]) extends NamespaceRepo.Service {

  override def list: Task[Vector[Namespace]] =
    SQL.list.transact(tr)

  override def create(
    params: CreateNamespaceParams
  ): Task[Either[String, Namespace]] =
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
         """.update
        .withGeneratedKeys[Int]("id")
        .compile
        .last
  }
}
