package sigil.service

import sigil.model.Flag

trait FlagService[F[_]] {
  def list: F[Vector[Flag]]
  def get(id: Int): F[Option[Flag]]
}
