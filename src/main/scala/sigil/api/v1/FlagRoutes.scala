package sigil.api.v1

import akka.http.scaladsl.server.Route
import sigil.BaseRouter
import sigil.service.FlagService

final class FlagRoutes(flagService: FlagService) extends BaseRouter {
  override def route: Route = ???
}
