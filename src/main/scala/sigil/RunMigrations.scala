package sigil

import org.flywaydb.core.Flyway
import sigil.config.DbConfig
import zio.RIO
import zio.blocking.{Blocking, effectBlocking}

object RunMigrations {
  def apply(dbConfig: DbConfig): RIO[Blocking, Int] = effectBlocking {
    Flyway
      .configure()
      .dataSource(dbConfig.url, dbConfig.username, dbConfig.password)
      .load()
      .migrate()
  }
}
