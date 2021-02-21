//package sigil.api.v1
//
//import akka.http.scaladsl.model.StatusCodes
//import akka.http.scaladsl.server.Route
//import akka.http.scaladsl.server.Directives._
//import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._
//import sigil.api.v1.params.CreateFlagParams._
//import cats.data.Validated
//import io.circe.Json
//import sigil.BaseRouter
//import sigil.api.v1.params.{CreateFlagParams, FlagsParams}
//
//final class FlagRoutes(flagService: FlagService[Future]) extends BaseRouter {
//  override def route: Route = pathPrefix("api" / "v1" / "flags") {
//    getFlag ~ list ~ create
//  }
//
//  private def list: Route = {
//    (get & listParameters) {
//      (limit,
//       offset,
//       enabled,
//       description,
//       tags,
//       descriptionLike,
//       key,
//       preload,
//       deleted) =>
//        FlagsParams(
//          limit,
//          offset,
//          enabled,
//          description,
//          tags,
//          descriptionLike,
//          key,
//          preload,
//          deleted
//        ).isValid match {
//
//          case Validated.Valid(_) =>
//            onSuccess(flagService.list) { list =>
//              complete(list)
//            }
//
//          case Validated.Invalid(_) =>
//            complete(StatusCodes.BadRequest, "loh")
//        }
//    }
//  }
//
//  private def getFlag: Route =
//    (get & path(IntNumber)) { id: Int =>
//      onSuccess(flagService.get(id)) {
//        case Some(flag) => complete(flag)
//        case None       => complete(StatusCodes.NotFound, "flag not found")
//      }
//    }
//
//  private def create: Route = {
//    import io.circe.syntax.EncoderOps
//    (post & entity(as[CreateFlagParams])) { params =>
//      params.isValid match {
//        case Validated.Valid(_) =>
//          onSuccess(flagService.create(params)) {
//            case Some(flag) => complete(StatusCodes.Created, flag)
//            case None       => complete(StatusCodes.UnprocessableEntity)
//          }
//
//        case Validated.Invalid(errors) =>
//          complete(
//            StatusCodes.UnprocessableEntity,
//            Map[String, Json]("errors" -> errors.asJson)
//          )
//      }
//    }
//  }
//
//  private val listParameters = parameters(
//    "limit".as[Int].optional,
//    "offset".as[Int].optional,
//    "enabled".as[Boolean].optional,
//    "description".as[String].optional,
//    "tags".as[String].optional,
//    "description_like".as[String].optional,
//    "key".as[String].optional,
//    "preload".as[Boolean].optional,
//    "deleted".as[Boolean].optional
//  )
//}

package sigil.api.v1

import io.circe.{Decoder, Encoder}
import org.http4s.circe._
import org.http4s.{EntityDecoder, EntityEncoder, HttpRoutes}
import org.http4s.dsl.Http4sDsl
import sigil.service.FlagService.FlagService
import zio.{Has, Task, ZLayer}
import zio.interop.catz._

object FlagRoutes {
  type FlagRoutes = Has[Service]
  trait Service {
    def route: HttpRoutes[Task]
  }

  implicit def circeJsonDecoder[A](
    implicit decoder: Decoder[A]
  ): EntityDecoder[Task, A] = jsonOf[Task, A]
  implicit def circeJsonEncoder[A](
    implicit decoder: Encoder[A]
  ): EntityEncoder[Task, A] =
    jsonEncoderOf[Task, A]

  val live: ZLayer[FlagService, Nothing, FlagRoutes] = ZLayer.fromService {
    srv =>
      val dsl: Http4sDsl[Task] = Http4sDsl[Task]
      import dsl._

      new Service {
        override def route: HttpRoutes[Task] = HttpRoutes.of[Task] {
          case GET -> Root / "v1" / "flags" => Ok(srv.list)
        }
      }
  }
}
