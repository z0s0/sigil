package sigil.api.v1.params

import cats.data.Validated
import io.circe.generic.JsonCodec
import sigil.api.ApiParams

@JsonCodec final case class CreateNamespaceParams(name: String) extends ApiParams {
  def validate: Validated[List[String], String] =
    Validated.cond(
      name.nonEmpty,
      "ok",
      List("namespace's name must be present")
    )
}
