package sigil.repo

import sigil.model.Flag

trait FlagRepo[F[_]] {
  def list: F[Vector[Flag]]
  def get(id: Int): F[Option[Flag]]
}
