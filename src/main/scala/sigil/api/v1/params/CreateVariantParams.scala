package sigil.api.v1.params

import cats.data.Validated
import io.circe.generic.JsonCodec
import sigil.api.ApiParams

@JsonCodec
final case class CreateVariantParams(key: String, attachment: Option[String]) extends ApiParams {
  def validate: Validated[List[String], String] =
    Validated.cond(key.nonEmpty, "ok", List("key must be provided"))
}
