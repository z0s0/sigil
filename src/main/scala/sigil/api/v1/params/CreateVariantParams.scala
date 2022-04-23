package sigil.api.v1.params

import cats.data.Validated
import io.circe.generic.JsonCodec
import sigil.api.ApiParams

@JsonCodec
final case class CreateVariantParams(flagId: Int, key: String, attachment: Option[String])
    extends ApiParams {
  def validate: Validated[List[String], String] =
    Validated.cond(key.length > 0, "ok", List("key must be provided"))
}
