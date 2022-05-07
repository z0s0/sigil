package sigil.repo

import cats.effect.IO
import doobie.util.transactor.Transactor
import sigil.api.v1.params.CreateSegmentParams
import sigil.model.Segment
import sigil.repo.impl.pg.SegmentRepoPgImpl

trait SegmentRepo {
  def createSegment(flagId: Int, params: CreateSegmentParams): IO[Option[Segment]]
  def upsertWithOrder(flagId: Int, segmentIds: List[Int]): IO[Unit]
}

object SegmentRepo {
  def of(transactor: Transactor[IO], supportedStorage: SupportedStorage) = supportedStorage match {
    case PG => new SegmentRepoPgImpl(transactor)
  }
}
