package sigil.api.v1

import cats.data.Validated
import cats.effect.IO
import io.circe.Json
import org.http4s.HttpRoutes
import sigil.api.v1.params.CreateFlagParams
import sigil.service.FlagService
import sigil.repo.DbError._
import sigil.api.JsonOps._
import org.http4s.dsl.io._

object FlagRoutes {
  def of(srv: FlagService): HttpRoutes[IO] = HttpRoutes.of[IO] {
    case GET -> Root / "v1" / "flags" / IntVar(id) =>
      srv.get(id).flatMap {
        case Some(flag) => Ok(flag)
        case None       => NotFound("Flag not found")
      }

    case GET -> Root / "v1" / "flags" => Ok(srv.list)

    case req @ POST -> Root / "v1" / "flags" =>
      req.decode[CreateFlagParams] { params =>
        params.validate match {
          case Validated.Valid(_) =>
            srv.create(params).flatMap {
              case Left(value)  => BadRequest(value)
              case Right(value) => Ok(value)
            }
          case Validated.Invalid(errors) =>
            UnprocessableEntity(
              Map[String, Json]("errors" -> params.leftToJson(errors))
            )
        }
      }
  }

}
