package sigil.api.v1.params

import cats.data.Validated
import sigil.api.ApiParams

final case class CreateNamespaceParams(name: String) extends ApiParams {
  def isValid: Validated[List[String], String] =
    Validated.cond(
      name.nonEmpty,
      "ok",
      List("namespace's name must be present")
    )
}
