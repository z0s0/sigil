package sigil.repo.impl.pg

import cats.data.OptionT
import cats.effect.IO
import doobie.Transactor
import doobie.implicits.toSqlInterpolator
import sigil.repo.SegmentRepo
import cats.implicits._
import doobie.implicits._
import sigil.api.v1.params.CreateSegmentParams
import sigil.model.Segment

final class SegmentRepoPgImpl(tr: Transactor[IO]) extends SegmentRepo {
  def createSegment(flagId: Int, params: CreateSegmentParams): IO[Option[Segment]] =
    (for {
      _ <- OptionT(sql"select 1 from flags where id = ${flagId}".query[Int].option)
      maxRank <- OptionT.liftF {
        sql"select max(rank) from segments where flag_id = ${flagId}"
          .query[Int]
          .option
          .map(_.getOrElse(0))
      }
      id <- OptionT.liftF {
        sql"""
              insert into segments (description, rank, rollout_ppm, flag_id) values
               (${params.description}, ${maxRank + 1}, ${params.rolloutPpm}, ${flagId})
          """.update.withUniqueGeneratedKeys[Int]("id")
      }
    } yield Segment(
      id = id,
      rank = maxRank + 1,
      rollOut = params.rolloutPpm,
      description = params.description
    )).value.transact(tr)

  def upsertWithOrder(flagId: Int, segmentIds: List[Int]): IO[Unit] =
    (for {
      _ <- sql"update segments set rank = rank * 10 where flag_id = ${flagId}".update.run
      _ <-
        segmentIds
          .zipWithIndex
          .traverse {
            case (id, idx) =>
              sql"update segments set rank = ${idx} where id = ${id}".update.run
          }
    } yield ()).transact(tr)

}
