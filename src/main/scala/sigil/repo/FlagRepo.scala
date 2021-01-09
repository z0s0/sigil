package sigil.repo

import sigil.model.Flag

trait FlagRepo[F[_]] {
  def list: F[Vector[Flag]]
}
