package sigil.api

import sigil.service.Layer.Services
import zio.{Has, ULayer, ZLayer}

object Layer {
  val live: ULayer[Has[Int]] = ZLayer.succeed(21)
}
