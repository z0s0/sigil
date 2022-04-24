package sigil.repo

import cats.effect.IO
import doobie.util.transactor.Transactor
import sigil.api.v1.params.CreateNamespaceParams
import sigil.model.Namespace
import sigil.repo.impl.pg.NamespaceRepoPGImpl

trait NamespaceRepo {
  def list: IO[Vector[Namespace]]
  def create(params: CreateNamespaceParams): IO[Either[MutationError, Namespace]]
}

object NamespaceRepo {
  def of(transactor: Transactor[IO], storage: SupportedStorage): NamespaceRepo =
    storage match { case PG => new NamespaceRepoPGImpl(transactor) }
}
