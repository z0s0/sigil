package sigil.api.v1.params

import cats.data.Validated
import io.circe.generic.JsonCodec
import sigil.api.ApiParams

@JsonCodec final case class CreateFlagParams(
  description: String,
  namespaceId: Int,
  key: Option[String],
  template: Option[String]
) extends ApiParams {
  def isValid: Validated[List[String], String] =
    Validated.cond(
      description.length > 0,
      "ok",
      List("description must be present")
    )
}
