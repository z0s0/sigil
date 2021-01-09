package sigil.repo.impl.pg

import cats.effect.IO
import doobie.util.transactor.Transactor
import sigil.model.Flag
import sigil.repo.FlagRepo
import doobie._
import doobie.implicits._
import sigil.repo.impl.pg.FlagRepoPGImpl.FlagRow

import scala.concurrent.Future

object FlagRepoPGImpl {
  final case class FlagRow(key: String) {
    def toFlag: Flag = ???
  }
}

class FlagRepoPGImpl(tr: Transactor[IO]) extends FlagRepo[Future] {
  def list: Future[Vector[Flag]] =
    SQL.list
      .transact(tr)
      .unsafeToFuture()

  object SQL {
    def list: ConnectionIO[Vector[Flag]] =
      sql"""
           select id, key, description from flags
         """
        .query[FlagRow]
        .map(_.toFlag)
        .to[Vector]
  }
}
