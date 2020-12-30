package sigil

import akka.http.scaladsl.server.Route

trait BaseRouter {
  def route: Route
}
