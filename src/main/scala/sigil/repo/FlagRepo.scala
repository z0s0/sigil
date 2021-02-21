package sigil.repo

import sigil.api.v1.params.{
  CreateFlagParams,
  CreateSegmentParams,
  CreateVariantParams
}
import sigil.config.DBConnection.DBTransactor
import sigil.model.{Flag, Segment, Variant}
import sigil.repo.impl.pg.FlagRepoPGImpl
import zio.{Has, Task, ZLayer}

object FlagRepo {
  type FlagRepo = Has[Service]

  val live: ZLayer[DBTransactor, Throwable, FlagRepo] = ZLayer.fromService {
    tr =>
      new FlagRepoPGImpl(tr)
  }

  trait Service {
    def list: Task[Vector[Flag]]
    def get(id: Int): Task[Option[Flag]]
    def create(params: CreateFlagParams): Task[Option[Flag]]

    def createVariant(
      params: CreateVariantParams
    ): Task[Either[String, Variant]]
    def createSegment(
      params: CreateSegmentParams
    ): Task[Either[String, Segment]]

    def deleteVariant(variantId: Int): Task[Either[String, Int]]
    def deleteSegment(segmentId: Int): Task[Either[String, Int]]
  }
}
