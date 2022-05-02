package sigil.repo.impl.pg

import cats.effect.IO
import doobie.Transactor
import doobie.implicits.toSqlInterpolator
import sigil.repo.SegmentRepo
import cats.syntax.apply._
import doobie.Query0
import doobie.implicits._

final class SegmentRepoPgImpl(transactor: Transactor[IO]) extends SegmentRepo {
  def upsertWithOrder(segmentIds: Vector[Int]): IO[Unit] = ???
}
