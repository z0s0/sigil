package sigil.config

import zio.{Has, ZLayer}

object Layer {
  val live: ZLayer[Any, Throwable, Has[Config]] = ZLayer.succeed(
    Config(
      dbConfig =
        DbConfig(username = "serega", password = "22", url = "localhost"),
      apiConfig = ApiConfig(4444)
    )
  )
}
