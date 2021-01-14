package sigil.service.impl

import sigil.api.v1.params.CreateNamespaceParams
import sigil.model.Namespace
import sigil.repo.NamespaceRepo
import sigil.service.NamespaceService

import scala.concurrent.Future

final class NamespaceServiceImpl(repo: NamespaceRepo[Future])
    extends NamespaceService[Future] {
  override def list: Future[Vector[Namespace]] = repo.list
  override def create(
    params: CreateNamespaceParams
  ): Future[Either[String, Namespace]] = repo.create(params)
}
