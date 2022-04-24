package sigil.api.v1

import cats.data.Validated
import cats.effect.IO
import io.circe.Json
import org.http4s.HttpRoutes
import sigil.service.NamespaceService
import sigil.repo.DbError._
import sigil.api.JsonOps._
import sigil.api.v1.params.CreateNamespaceParams
import org.http4s.dsl.io._
import sigil.api.ApiParams.Decoders._

object NamespaceRoutes {
  def of(srv: NamespaceService): HttpRoutes[IO] = HttpRoutes.of[IO] {
    case GET -> Root / "v1" / "namespaces" => Ok(srv.list)
    case req @ POST -> Root / "v1" / "namespaces" =>
      req.decode[CreateNamespaceParams] { params =>
        params.validate match {
          case Validated.Valid(_) =>
            srv.create(params).flatMap {
              case Left(value)  => BadRequest(value)
              case Right(value) => Created(value)
            }

          case Validated.Invalid(e) =>
            BadRequest(
              Map[String, Json]("errors" -> params.leftToJson(e))
            )
        }
      }
  }
}
