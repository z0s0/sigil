package sigil.api.v1

import cats.data.Validated
import io.circe.{Decoder, Encoder, Json}
import org.http4s.circe._
import org.http4s.{EntityDecoder, EntityEncoder, HttpRoutes}
import org.http4s.dsl.Http4sDsl
import sigil.api.v1.params.CreateFlagParams
import sigil.service.FlagService.FlagService
import zio.{Has, Task, ZLayer}
import zio.interop.catz._
import sigil.api.v1.params.CreateFlagParams._

object FlagRoutes {
  type FlagRoutes = Has[Service]
  trait Service {
    def route: HttpRoutes[Task]
  }

  implicit def circeJsonDecoder[A](
    implicit decoder: Decoder[A]
  ): EntityDecoder[Task, A] = jsonOf[Task, A]
  implicit def circeJsonEncoder[A](
    implicit decoder: Encoder[A]
  ): EntityEncoder[Task, A] =
    jsonEncoderOf[Task, A]

  val live: ZLayer[FlagService, Nothing, FlagRoutes] = ZLayer.fromService {
    srv =>
      val dsl: Http4sDsl[Task] = Http4sDsl[Task]
      import dsl._

      new Service {
        override def route: HttpRoutes[Task] = HttpRoutes.of[Task] {
          case GET -> Root / "v1" / "flags" / IntVar(id) =>
            srv.get(id).flatMap {
              case Some(flag) => Ok(flag)
              case None       => NotFound("Flag not found")
            }

          case GET -> Root / "v1" / "flags" => Ok(srv.list)

          case req @ POST -> Root / "v1" / "flags" =>
            req.decode[CreateFlagParams] { params =>
              params.isValid match {
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
  }
}
