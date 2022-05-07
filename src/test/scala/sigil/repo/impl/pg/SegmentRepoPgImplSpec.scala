//package sigil.repo.impl.pg
//
//import cats.data.EitherT
//import cats.effect.IO
//import org.scalatest.freespec.AnyFreeSpec
//import sigil.api.v1.params.{CreateFlagParams, CreateSegmentParams}
//import support.PostgreSqlContainer
//import cats.effect.unsafe.implicits.global
//import sigil.repo.MutationError
//
//final class SegmentRepoPgImplSpec extends AnyFreeSpec {
//  "createSegment" - {
//    "when flag has no segments" in {
//      PostgreSqlContainer
//        .setupTransactor()
//        .use { tr =>
//          val segmentRepo = new SegmentRepoPgImpl(tr)
//          val flagRepo = new FlagRepoPGImpl(tr)
//
//          (for {
//            flag <- EitherT(flagRepo.create(CreateFlagParams.Test))
//            segment <- EitherT.liftF(segmentRepo.createSegment(flag.id, CreateSegmentParams.Test))
//          } yield {
//            assert(segment.rank == 1)
//            IO.unit
//          }).value.unsafeRunSync()
//
//        }
//        .unsafeRunSync()
//    }
//    "when flag already has segments" in {
//      ???
//    }
//  }
//
//  "updateWithOrder" - {}
//}
