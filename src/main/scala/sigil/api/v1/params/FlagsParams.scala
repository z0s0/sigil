package sigil.api.v1.params

import cats.data.Validated
import sigil.api.ApiParams

final case class FlagsParams(
  lim: Option[Int],
  offset: Option[Int],
  enabled: Option[Boolean],
  description: Option[String],
  tags: Option[String],
  descriptionLike: Option[String],
  key: Option[String],
  preload: Option[Boolean],
  deleted: Option[Boolean]
) extends ApiParams {
  def validate: Validated[List[String], String] = {
    Validated.cond(
      lim.getOrElse(0) >= 0,
      "ok",
      List("limit must not be negative")
    )
  }
}
