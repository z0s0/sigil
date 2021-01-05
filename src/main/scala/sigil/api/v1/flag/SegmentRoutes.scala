package sigil.api.v1.flag

import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.Directives._
import sigil.BaseRouter

final class SegmentRoutes extends BaseRouter {
  override def route: Route =
    pathPrefix("api" / "v1" / "flags") {
      list ~ create ~ update ~ delete
    }

  private def list: Route = ???
  private def create: Route = ???
  private def update: Route = ???
  private def delete: Route = ???
}
