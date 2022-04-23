package sigil

import cats.effect.IO
import org.flywaydb.core.Flyway
import sigil.config.DbConfig

object RunMigrations {
  def apply(dbConfig: DbConfig) = IO.blocking {
    Flyway
      .configure()
      .dataSource(dbConfig.url, dbConfig.username, dbConfig.password)
      .load()
      .migrate()
  }
}
