//package sigil.api.v1
//
//import cats.data.Validated
//import io.circe.{Decoder, Encoder, Json}
//import org.http4s.circe.CirceEntityCodec.circeEntityEncoder
//import org.http4s.circe._
//import org.http4s.{EntityDecoder, EntityEncoder, HttpRoutes}
//import sigil.api.v1.params.CreateFlagParams
//import zio.Task
//import sigil.service.FlagService
//import sigil.model.Flag._
//import org.http4s.dsl.io._
//import zio.interop.catz._
//
//object FlagRoutes {
//  implicit def circeJsonDecoder[A: Decoder]: EntityDecoder[Task, A] = jsonOf[Task, A]
//  implicit def circeJsonEncoder[A: Encoder]: EntityEncoder[Task, A] = jsonEncoderOf[Task, A]
//
//  def live(srv: FlagService.Service): HttpRoutes[Task] = HttpRoutes.of[Task] {
//    case GET -> Root / "v1" / "flags" / IntVar(id) =>
//      srv.get(id).flatMap {
//        case Some(flag) => Ok(flag)
//        case None       => NotFound("Flag not found")
//      }
//
//    case GET -> Root / "v1" / "flags" => Ok(srv.list)
//
//    case req @ POST -> Root / "v1" / "flags" =>
//      req.decode[CreateFlagParams] { params =>
//        params.isValid match {
//          case Validated.Valid(_) =>
//            srv.create(params).flatMap {
//              case Some(flag) =>
//                Created(flag)
//              case None =>
//                UnprocessableEntity()
//            }
//          case Validated.Invalid(errors) =>
//            UnprocessableEntity(
//              Map[String, Json]("errors" -> params.leftToJson(errors))
//            )
//        }
//      }
//  }
//
//}
