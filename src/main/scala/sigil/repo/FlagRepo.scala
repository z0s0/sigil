package sigil.repo

import com.datastax.driver.core.schemabuilder.Create
import sigil.api.v1.params.CreateFlagParams
import sigil.model.Flag

trait FlagRepo[F[_]] {
  def list: F[Vector[Flag]]
  def get(id: Int): F[Option[Flag]]
  def create(params: CreateFlagParams): F[Option[Flag]]
}
