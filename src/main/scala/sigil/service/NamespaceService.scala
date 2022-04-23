package sigil.service

import sigil.api.v1.params.CreateNamespaceParams
import sigil.model.Namespace
import sigil.repo.NamespaceRepo
import zio.Task

trait NamespaceService {
  def list: Task[Vector[Namespace]]
  def create(params: CreateNamespaceParams): Task[Either[String, Namespace]]
}

object NamespaceService {
  def of(repo: NamespaceRepo): NamespaceService = new NamespaceService {
    def list: Task[Vector[Namespace]] = repo.list

    def create(params: CreateNamespaceParams): Task[Either[String, Namespace]] = repo.create(params)
  }

}
