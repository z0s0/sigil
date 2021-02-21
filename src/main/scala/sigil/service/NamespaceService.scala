package sigil.service

import sigil.api.v1.params.CreateNamespaceParams
import sigil.model.Namespace
import sigil.repo.NamespaceRepo.NamespaceRepo
import sigil.service.impl.NamespaceServiceImpl
import zio.{Has, Task, ZLayer}

object NamespaceService {
  type NamespaceService = Has[Service]

  trait Service {
    def list: Task[Vector[Namespace]]
    def create(params: CreateNamespaceParams): Task[Either[String, Namespace]]
  }

  val live: ZLayer[NamespaceRepo, Nothing, NamespaceService] =
    ZLayer.fromService(new NamespaceServiceImpl(_))
}
