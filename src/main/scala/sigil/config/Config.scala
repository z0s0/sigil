package sigil.config

import zio.{Has, Layer, Task, ZLayer}
import pureconfig.ConfigSource
import pureconfig.generic.auto._

final case class Config(dbConfig: DbConfig, apiConfig: ApiConfig)
final case class ApiConfig(port: Int)
final case class DbConfig(username: String, password: String, url: String)

object Config {
  type AllConfigs = Has[ApiConfig] with Has[DbConfig]

  val live: Layer[Throwable, AllConfigs] =
    ZLayer.fromEffectMany(
      Task
        .effect(ConfigSource.default.loadOrThrow[Config])
        .map(c => Has(c.apiConfig) ++ Has(c.dbConfig))
    )
}
