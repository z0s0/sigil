package sigil.service.impl

import sigil.api.v1.params.CreateNamespaceParams
import sigil.model.Namespace
import sigil.repo.NamespaceRepo
import sigil.service.NamespaceService
import zio.Task

final class NamespaceServiceImpl(repo: NamespaceRepo.Service)
    extends NamespaceService.Service {
  override def list: Task[Vector[Namespace]] = repo.list
  override def create(
    params: CreateNamespaceParams
  ): Task[Either[String, Namespace]] = repo.create(params)
}
