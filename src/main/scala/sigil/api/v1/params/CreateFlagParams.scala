package sigil.api.v1.params

import cats.data.Validated
import sigil.api.ApiParams
import io.circe.generic.semiauto.deriveDecoder

object CreateFlagParams {
  implicit val jsonDecoder = deriveDecoder[CreateFlagParams]
}

final case class CreateFlagParams(description: String,
                                  namespaceId: Int,
                                  key: Option[String],
                                  template: Option[String])
    extends ApiParams {
  override def isValid: Validated[List[String], String] =
    Validated.cond(
      description.length > 0,
      "ok",
      List("description must be present")
    )
}
