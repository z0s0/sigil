package sigil.api.v1.params

import cats.data.Validated
import io.circe.generic.JsonCodec
import sigil.api.ApiParams

@JsonCodec final case class EvalParams(
  entityId: Option[String],
  entityType: Option[String],
  entityContext: Option[String],
  enableDebug: Option[Boolean],
  flagId: Option[Int],
  flagKey: Option[String],
  flagTags: Option[List[String]],
  flagTagsOperator: Option[String] // "ANY" or "ALL"
) extends ApiParams {
  def validate: Validated[List[String], String] =
    Validated.cond(
      flagKey.nonEmpty || flagId.nonEmpty,
      "ok",
      List("flagKey or flagId must be present")
    )
}
