package sigil.api

import cats.data.Validated
import io.circe.generic.semiauto.deriveDecoder
import io.circe.{Decoder, Json}
import io.circe.syntax.EncoderOps
import sigil.api.v1.params._

abstract class ApiParams {
  def validate: Validated[List[String], String]

  def leftToJson(errors: List[String]): Json = errors.asJson
}

object ApiParams {
  object Decoders {
    implicit val d1: Decoder[CreateFlagParams] = deriveDecoder
    implicit val d2: Decoder[CreateNamespaceParams] = deriveDecoder
    implicit val d3: Decoder[CreateSegmentParams] = deriveDecoder
    implicit val d4: Decoder[CreateVariantParams] = deriveDecoder
    implicit val d5: Decoder[FlagsParams] = deriveDecoder
  }
}
