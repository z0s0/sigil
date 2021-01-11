package sigil.api.v1.params

import cats.data.Validated
import io.circe.Decoder
import sigil.api.ApiParams
import io.circe.generic.semiauto.deriveDecoder

object CreateNamespaceParams {
  implicit val jsonDecoder: Decoder[CreateNamespaceParams] = deriveDecoder
}

final case class CreateNamespaceParams(name: String) extends ApiParams {
  override def isValid: Validated[List[String], String] =
    Validated.cond(
      name.length > 0,
      "ok",
      List("namespace's name must be present")
    )
}
