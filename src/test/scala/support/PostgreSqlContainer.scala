package support

import cats.effect.IO
import cats.effect.kernel.Resource
import com.dimafeng.testcontainers.PostgreSQLContainer
import doobie.Transactor
import sigil.RunMigrations
import sigil.config.{DBConnection, DbConfig}
import sigil.repo.{PG, SupportedStorage}

object PostgreSqlContainer {
  def setupTransactor(): Resource[IO, Transactor[IO]] = {
    val container = Resource.make {
      IO {
        val cont = PostgreSQLContainer("postgres:latest").container
        cont.start()
        cont
      }
    } { cont => IO(cont.stop()) }

    for {
      cont <- container
      conf = DbConfig(
        username = cont.getUsername,
        password = cont.getPassword,
        url = cont.getJdbcUrl
      )

      _ <- Resource.eval(RunMigrations(conf, PG))
      transactor <- Resource.eval(IO(DBConnection.of(conf)))
    } yield transactor
  }
}
