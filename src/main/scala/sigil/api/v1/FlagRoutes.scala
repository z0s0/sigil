package sigil.api.v1

import cats.data.Validated
import cats.effect.IO
import io.circe.{Decoder, Encoder, Json}
import org.http4s.circe._
import org.http4s.{EntityDecoder, EntityEncoder, HttpRoutes}
import sigil.api.v1.params.CreateFlagParams
import sigil.service.FlagService
import org.http4s.dsl.io._

object FlagRoutes {
  implicit def circeJsonDecoder[A: Decoder]: EntityDecoder[IO, A] = jsonOf[IO, A]
  implicit def circeJsonEncoder[A: Encoder]: EntityEncoder[IO, A] = jsonEncoderOf[IO, A]

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
              case Some(flag) =>
                Created(flag)
              case None =>
                UnprocessableEntity()
            }
          case Validated.Invalid(errors) =>
            UnprocessableEntity(
              Map[String, Json]("errors" -> params.leftToJson(errors))
            )
        }
      }
  }

}
