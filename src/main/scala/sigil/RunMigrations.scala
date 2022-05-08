package sigil

import cats.effect.IO
import org.flywaydb.core.Flyway
import sigil.config.DbConfig
import sigil.repo.{PG, SupportedStorage}

object RunMigrations {
  def apply(dbConfig: DbConfig, supportedStorage: SupportedStorage) = supportedStorage match {
    case PG =>
      IO.blocking {
        Flyway
          .configure()
          .dataSource(dbConfig.url, dbConfig.username, dbConfig.password)
          .load()
          .migrate()
      }
  }
}
