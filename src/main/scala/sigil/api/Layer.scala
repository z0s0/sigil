package sigil.api

import sigil.service.Layer.ServiceLayer
import zio.{Has, ZLayer}

object Layer {
  type APILayer = Has[Int]
  val live: ZLayer[ServiceLayer, Throwable, APILayer] = ZLayer.succeed(99)
}
