package sigil.service

import sigil.cache.Layer.CacheLayer
import sigil.repo.Layer.PersistenceLayer
import zio.{Has, ZLayer}

object Layer {
  type ServiceLayer = Has[Int]
  val live: ZLayer[CacheLayer with PersistenceLayer, Throwable, ServiceLayer] =
    ZLayer.succeed(12)
}
