package sigil.service

import sigil.api.v1.params.CreateNamespaceParams
import sigil.model.Namespace

trait NamespaceService[F[_]] {
  def list: F[Vector[Namespace]]
  def create(params: CreateNamespaceParams): F[Either[String, Namespace]]
}
