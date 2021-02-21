package sigil.config

import cats.effect.Blocker
import doobie.hikari.HikariTransactor
import doobie.util.transactor.Transactor
import zio.{Has, Managed, Task, ZIO, ZLayer}

import scala.concurrent.ExecutionContext

object DBConnection {

  type DBTransactor = Has[Transactor[Task]]

  trait Service

  def mkTransactor(
    config: DbConfig,
    connectEC: ExecutionContext,
    transactEC: ExecutionContext
  ): Managed[Throwable, Transactor[Task]] = {
    import zio.interop.catz._

    HikariTransactor
      .newHikariTransactor[Task](
        "org.postgresql.Driver",
        config.url,
        config.username,
        config.password,
        connectEC,
        Blocker.liftExecutionContext(transactEC)
      )
      .toManagedZIO
  }

  val live: ZLayer[Has[DbConfig], Throwable, DBTransactor] = {
    ???
  }

}
