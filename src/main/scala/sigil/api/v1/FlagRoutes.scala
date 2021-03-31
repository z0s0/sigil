package sigil.api.v1

import cats.data.Validated
import io.circe.{Decoder, Encoder, Json}
import org.http4s.circe._
import org.http4s.{EntityDecoder, EntityEncoder, HttpRoutes, QueryParamDecoder}
import org.http4s.dsl.Http4sDsl
import sigil.api.v1.params.{CreateFlagParams, FlagsListParams}
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

          case GET -> Root / "v1" / "flags"
                :? limit(lim)
//                  +& offset(offset)
                  +& enabled(enabled)
//                  +& deleted(deleted)
//                  +& descriptionLike(descriptionLike)
//                  +& tags(tags)
                  +& key(key) =>
//                  +& description(description)
//                  +& preload(preload) =>
            val params = List(lim, enabled, key)
              .withFilter {
                case Some(_) => true
                case None    => false
              }
              .map(_.get)

            println(params)

            Ok(srv.list(params))

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

        implicit val keyDecoder =
          QueryParamDecoder[String].map(FlagsListParams.Key)
        implicit val enabledDecoder =
          QueryParamDecoder[Boolean].map(FlagsListParams.Enabled)
        implicit val limitDecoder =
          QueryParamDecoder[Int].map(FlagsListParams.Limit)

        private object limit
            extends OptionalQueryParamDecoderMatcher[FlagsListParams.Limit](
              "limit"
            )
        private object description
            extends OptionalQueryParamDecoderMatcher[String]("description")
        private object tags
            extends OptionalQueryParamDecoderMatcher[String]("tags")
        private object descriptionLike
            extends OptionalQueryParamDecoderMatcher[String]("description_like")
        private object key
            extends OptionalQueryParamDecoderMatcher[FlagsListParams.Key]("key")
        private object offset
            extends OptionalQueryParamDecoderMatcher[Int]("offset")
        private object enabled
            extends OptionalQueryParamDecoderMatcher[FlagsListParams.Enabled](
              "enabled"
            )
        private object deleted
            extends OptionalQueryParamDecoderMatcher[Boolean]("deleted")
        private object preload
            extends OptionalQueryParamDecoderMatcher[Boolean]("preload")
      }
  }
}
