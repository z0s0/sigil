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
  final case class FlagRow(id: Int,
                           key: String,
                           description: String,
                           enabled: Boolean,
                           notes: String) {
    def toFlag: Flag = Flag(
      id = id,
      key = key,
      description = description,
      enabled = enabled,
      notes = notes
    )
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
           select id, key, description, enabled, notes from flags
         """
        .query[FlagRow]
        .map(_.toFlag)
        .to[Vector]
  }
}
