package sigil.repo

import doobie.util.transactor.Transactor
import sigil.api.v1.params.CreateNamespaceParams
import sigil.model.Namespace
import sigil.repo.impl.pg.NamespaceRepoPGImpl
import zio.Task

trait NamespaceRepo {
  def list: Task[Vector[Namespace]]
  def create(params: CreateNamespaceParams): Task[Either[String, Namespace]]
}

object NamespaceRepo {
  def of(transactor: Transactor[Task]): NamespaceRepo = new NamespaceRepoPGImpl(transactor)
}
