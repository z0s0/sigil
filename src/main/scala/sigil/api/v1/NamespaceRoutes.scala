package sigil.api.v1

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.Directives._
import cats.data.Validated
import sigil.BaseRouter
import sigil.service.NamespaceService
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._
import sigil.api.v1.params.CreateNamespaceParams

import scala.concurrent.Future

final class NamespaceRoutes(namespaceService: NamespaceService[Future])
    extends BaseRouter {
  override def route: Route =
    pathPrefix("api" / "v1" / "namespaces") {
      list ~ create
    }

  private def list: Route =
    get {
      onSuccess(namespaceService.list) { list =>
        complete(list)
      }
    }

  private def create: Route =
    (post & entity(as[CreateNamespaceParams])) { params =>
      params.isValid match {
        case Validated.Valid(_) =>
          onSuccess(namespaceService.create(params)) {
            case Left(errors) =>
              complete(params.leftToJson(List(errors)))
            case Right(namespace) =>
              complete(StatusCodes.Created, namespace)

          }
        case Validated.Invalid(err) =>
          complete(StatusCodes.UnprocessableEntity, params.leftToJson(err))
      }
    }
}
