package sigil.repo

import cats.effect.IO
import doobie.util.transactor.Transactor
import sigil.repo.impl.pg.SegmentRepoPgImpl

trait SegmentRepo {
  def upsertWithOrder(segmentIds: Vector[Int]): IO[Unit]
}

object SegmentRepo {
  def of(transactor: Transactor[IO], supportedStorage: SupportedStorage) = supportedStorage match {
    case PG => new SegmentRepoPgImpl(transactor)
  }
}
