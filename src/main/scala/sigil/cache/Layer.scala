package sigil.cache

import sigil.config.Config
import zio.{Has, ZLayer}

object Layer {
  type CacheLayer = Has[Int]
  val live: ZLayer[Has[Config], Throwable, CacheLayer] = ZLayer.succeed(3)
}
