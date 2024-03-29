package sigil.config

import cats.effect.IO
import pureconfig.ConfigSource
import pureconfig.generic.auto._

final case class Config(dbConfig: DbConfig, apiConfig: ApiConfig, appConfig: AppConfig)
final case class ApiConfig(port: Int)
final case class DbConfig(username: String, password: String, url: String)
final case class AppConfig(persistence: String, pubChannel: String)

object Config {

  val load: IO[Config] = IO.blocking(ConfigSource.default.loadOrThrow[Config])
}
