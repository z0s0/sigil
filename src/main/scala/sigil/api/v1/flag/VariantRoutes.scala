package sigil.api.v1.flag

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.Directives._
import cats.data.Validated
import sigil.BaseRouter
import sigil.service.FlagService

import scala.concurrent.Future
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._
import sigil.api.v1.params.CreateVariantParams

final class VariantRoutes(flagService: FlagService[Future]) extends BaseRouter {
  override def route: Route =
    pathPrefix("api" / "v1" / "flags" / IntNumber / "variants") { flagId =>
      list(flagId) ~ create(flagId)
    }

  private def list(flagId: Int): Route =
    get {
      onSuccess(flagService.flagVariants(flagId))(v => complete(v))
    }

  private def create(flagId: Int): Route =
    (post & entity(as[CreateVariantParams])) { params =>
      params.isValid match {
        case Validated.Valid(_) =>
          onSuccess(flagService.createVariant(params)) {
            case Left(_)      => complete(StatusCodes.UnprocessableEntity)
            case Right(value) => complete(StatusCodes.Created, value)
          }

        case Validated.Invalid(e) =>
          complete(StatusCodes.BadRequest, params.leftToJson(e))
      }
    }
}
