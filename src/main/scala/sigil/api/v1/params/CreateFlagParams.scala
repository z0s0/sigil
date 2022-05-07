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
  def validate: Validated[List[String], String] =
    Validated.cond(
      description.nonEmpty,
      "ok",
      List("description must be present")
    )
}

object CreateFlagParams {
  val Test = CreateFlagParams(
    "test",
    1,
    Some("test"),
    None
  )
}
