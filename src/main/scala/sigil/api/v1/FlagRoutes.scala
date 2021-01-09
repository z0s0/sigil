package sigil.api.v1

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.Directives._
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._
import sigil.api.v1.params.CreateFlagParams._
import cats.data.Validated
import io.circe.Json
import sigil.BaseRouter
import sigil.api.v1.params.{CreateFlagParams, FlagsParams}
import sigil.service.FlagService

import scala.concurrent.Future
import scala.util.{Failure, Success}

final class FlagRoutes(flagService: FlagService[Future]) extends BaseRouter {
  override def route: Route = pathPrefix("api" / "v1") {
    list ~ create
  }

  private def list: Route = {
    (get & path("flags") & listParameters) {
      (limit,
       offset,
       enabled,
       description,
       tags,
       descriptionLike,
       key,
       preload,
       deleted) =>
        FlagsParams(
          limit,
          offset,
          enabled,
          description,
          tags,
          descriptionLike,
          key,
          preload,
          deleted
        ).isValid match {

          case Validated.Valid(_) =>
            onSuccess(flagService.list) { list =>
              complete(list)
            }

          case Validated.Invalid(_) =>
            complete(StatusCodes.BadRequest, "loh")
        }
    }
  }

  private def create: Route = {
    import io.circe.syntax.EncoderOps
    (post & entity(as[CreateFlagParams])) { params =>
      params.isValid match {
        case Validated.Valid(_) =>
          complete(StatusCodes.Created, "ok")
        case Validated.Invalid(errors) =>
          complete(
            StatusCodes.UnprocessableEntity,
            Map[String, Json]("errors" -> errors.asJson)
          )
      }
    }
  }

  private val listParameters = parameters(
    "limit".as[Int].optional,
    "offset".as[Int].optional,
    "enabled".as[Boolean].optional,
    "description".as[String].optional,
    "tags".as[String].optional,
    "description_like".as[String].optional,
    "key".as[String].optional,
    "preload".as[Boolean].optional,
    "deleted".as[Boolean].optional
  )
}
