package sigil.service

import sigil.api.v1.params.CreateFlagParams
import sigil.model.Flag

trait FlagService[F[_]] {
  def list: F[Vector[Flag]]
  def get(id: Int): F[Option[Flag]]
  def create(params: CreateFlagParams): F[Option[Flag]]
}
