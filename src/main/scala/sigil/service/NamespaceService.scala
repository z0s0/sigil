package sigil.service

import cats.effect.IO
import sigil.api.v1.params.CreateNamespaceParams
import sigil.model.Namespace
import sigil.repo.NamespaceRepo

trait NamespaceService {
  def list: IO[Vector[Namespace]]
  def create(params: CreateNamespaceParams): IO[Either[String, Namespace]]
}

object NamespaceService {
  def of(repo: NamespaceRepo): NamespaceService = new NamespaceService {
    def list: IO[Vector[Namespace]] = repo.list

    def create(params: CreateNamespaceParams): IO[Either[String, Namespace]] = repo.create(params)
  }

}
