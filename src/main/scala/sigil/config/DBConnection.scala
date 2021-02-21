package sigil.config

import cats.effect.Blocker
import doobie.hikari.HikariTransactor
import doobie.util.transactor.Transactor
import zio.blocking.{Blocking, blocking}
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

  val live: ZLayer[Has[DbConfig] with Blocking, Throwable, DBTransactor] =
    ZLayer.fromManaged(for {
      connectEC <- ZIO.descriptor.map(_.executor.asEC).toManaged_
      blockingEC <- blocking {
        ZIO.descriptor.map(_.executor.asEC)
      }.toManaged_
      conf <- ZIO.access[Has[DbConfig]](_.get).toManaged_
      transactor <- mkTransactor(conf, connectEC, blockingEC)
    } yield transactor)

}
