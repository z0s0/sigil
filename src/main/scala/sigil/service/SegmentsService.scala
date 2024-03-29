package sigil.service

import cats.effect.IO
import cats.implicits.{catsSyntaxApplicativeId, catsSyntaxEitherId}
import sigil.api.v1.params.CreateSegmentParams
import sigil.model.Segment
import sigil.repo.{FlagRepo, SegmentRepo}

trait SegmentsService {
  def createSegment(flagId: Int, params: CreateSegmentParams): IO[Either[String, Segment]]
  def reorder(flagId: Int, segmentIds: List[Int]): IO[Either[String, Unit]]
}

object SegmentsService {
  def of(flagRepo: FlagRepo, segmentRepo: SegmentRepo): SegmentsService = new SegmentsService {
    def createSegment(flagId: Int, params: CreateSegmentParams): IO[Either[String, Segment]] =
      segmentRepo.createSegment(flagId, params).map {
        case Some(segment) => segment.asRight[String]
        case None          => s"flag with id ${flagId} not found".asLeft[Segment]
      }

    def reorder(flagId: Int, segmentIds: List[Int]): IO[Either[String, Unit]] = {
      val segmentIdsSet = segmentIds.toSet
      val idsUnique = segmentIdsSet.size == segmentIds.size
      val flagSegmentIds: IO[Option[Vector[Int]]] = flagRepo.flagSegmentIds(flagId)

      if (idsUnique) {
        for {
          segmentIdsOpt <- flagSegmentIds
          result <- segmentIdsOpt match {
            case None =>
              s"flag with id ${flagId} doesn't exist".asLeft[Unit].pure[IO]
            case Some(ids) =>
              if (ids.toSet == segmentIdsSet) {
                segmentRepo.upsertWithOrder(flagId, segmentIds).map(_.asRight[String])
              } else {
                s"not all segmentIds belong to flag ${flagId}".asLeft[Unit].pure[IO]
              }
          }
        } yield result
      } else {
        "ids must be unique".asLeft[Unit].pure[IO]
      }
    }
  }
}
