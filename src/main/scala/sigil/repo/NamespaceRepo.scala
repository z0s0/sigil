package sigil.repo

import sigil.api.v1.params.CreateNamespaceParams
import sigil.config.DBConnection.DBTransactor
import sigil.model.Namespace
import sigil.repo.impl.pg.NamespaceRepoPGImpl
import zio.{Has, Task, ZLayer}

object NamespaceRepo {
  type NamespaceRepo = Has[Service]

  trait Service {
    def list: Task[Vector[Namespace]]
    def create(params: CreateNamespaceParams): Task[Either[String, Namespace]]
  }

  val live: ZLayer[DBTransactor, Nothing, NamespaceRepo] =
    ZLayer.fromService(new NamespaceRepoPGImpl(_))
}
