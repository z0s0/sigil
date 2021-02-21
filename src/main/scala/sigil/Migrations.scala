package sigil

import org.flywaydb.core.Flyway
import sigil.config.DbConfig

object Migrations {
  def run(dbConfig: DbConfig): Unit = {
    Flyway
      .configure()
      .dataSource(dbConfig.url, dbConfig.username, dbConfig.password)
      .load()
      .migrate()
  }
}
