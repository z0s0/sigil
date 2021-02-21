//package sigil.api.v1.flag
//
//import akka.http.scaladsl.model.StatusCodes
//import akka.http.scaladsl.server.Route
//import akka.http.scaladsl.server.Directives._
//import cats.data.Validated
//import sigil.BaseRouter
//import sigil.service.FlagService
//import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._
//import sigil.api.v1.params.CreateSegmentParams
//
//import scala.concurrent.Future
//
//final class SegmentRoutes(flagService: FlagService[Future]) extends BaseRouter {
//  override def route: Route =
//    pathPrefix("api" / "v1" / "flags" / IntNumber / "segments") { flagId =>
//      list(flagId) ~ create(flagId) ~ update(flagId) ~ remove(flagId)
//    }
//
//  private def list(flagId: Int): Route =
//    get {
//      onSuccess(flagService.flagSegments(flagId))(s => complete(s))
//    }
//
//  private def create(flagId: Int): Route =
//    (post & entity(as[CreateSegmentParams])) { params =>
//      params.isValid match {
//        case Validated.Valid(_) =>
//          onSuccess(flagService.createSegment(params)) {
//            case Left(_)        => complete(StatusCodes.UnprocessableEntity)
//            case Right(segment) => complete(StatusCodes.Created, segment)
//          }
//
//        case Validated.Invalid(e) =>
//          complete(StatusCodes.BadRequest, params.leftToJson(e))
//      }
//    }
//
//  private def update(flagId: Int): Route =
//    put {
//      complete("updated segment")
//    }
//
//  private def remove(flagId: Int): Route =
//    (delete & path(IntNumber)) { segmentId =>
//      onSuccess(flagService.deleteSegment(flagId, segmentId)) {
//        case Left(_)        => complete(StatusCodes.NotFound)
//        case Right(segment) => complete(StatusCodes.Found, segment)
//      }
//    }
//}
