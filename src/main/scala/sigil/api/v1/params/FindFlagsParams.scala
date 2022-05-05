package sigil.api.v1.params

import cats.data.Validated
import sigil.api.ApiParams
import sttp.tapir.EndpointIO.annotations.params

@params
final case class FindFlagsParams(
  limit: Option[Int],
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
      limit.getOrElse(0) >= 0,
      "ok",
      List("limit must not be negative")
    )
  }
}

object FindFlagsParams {
  val Empty = FindFlagsParams(None, None, None, None, None, None, None, None, None)
}
