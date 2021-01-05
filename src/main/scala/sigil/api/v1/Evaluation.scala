package sigil.api.v1

import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.Directives._

import sigil.BaseRouter

final class Evaluation extends BaseRouter {
  override def route: Route = pathPrefix("api" / "v1" / "evaluation") {
    evalBatch ~ eval
  }

  def evalBatch: Route =
    (post & path("batch")) {
      complete("batch")
    }

  def eval: Route =
    post {
      complete("eval")
    }

}
