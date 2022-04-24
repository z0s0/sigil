package sigil.api

import cats.effect.IO
import io.circe.{Decoder, Encoder}
import org.http4s.{EntityDecoder, EntityEncoder}
import org.http4s.circe.{jsonEncoderOf, jsonOf}

object JsonOps {
  implicit def circeJsonDecoder[A: Decoder]: EntityDecoder[IO, A] = jsonOf[IO, A]
  implicit def circeJsonEncoder[A: Encoder]: EntityEncoder[IO, A] = jsonEncoderOf[IO, A]
}
