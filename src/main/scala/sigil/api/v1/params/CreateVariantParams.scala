package sigil.api.v1.params

import cats.data.Validated
import sigil.api.ApiParams
import io.circe.Decoder
import io.circe.generic.semiauto.deriveDecoder

object CreateVariantParams {
  implicit val jsonDecoder: Decoder[CreateVariantParams] = deriveDecoder
}

final case class CreateVariantParams(flagId: Int,
                                     key: String,
                                     attachment: Option[String])
    extends ApiParams {
  override def isValid: Validated[List[String], String] =
    Validated.cond(key.length > 0, "ok", List("key must be provided"))
}
