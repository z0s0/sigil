package sigil.config

import cats.effect.IO
import doobie.util.transactor.Transactor
import doobie.util.transactor.Transactor.Aux

object DBConnection {

  def of(config: DbConfig): Aux[IO, Unit] = Transactor.fromDriverManager[IO](
    "org.postgresql.Driver",
    config.url,
    config.username,
    config.password
  )

}
