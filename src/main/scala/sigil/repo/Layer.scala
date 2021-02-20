package sigil.repo

import sigil.config.Config
import zio.{Has, ZLayer}

object Layer {
  type PersistenceLayer = Has[Int]
  def live: ZLayer[Has[Config], Throwable, PersistenceLayer] = ZLayer.succeed(1)
}
