package sigil.api.v1.params

import io.circe.generic.JsonCodec

@JsonCodec final case class EvalParams(
  entityId: Option[String],
  entityType: String,
  entityContext: Option[String],
  enableDebug: Option[Boolean],
  flagId: Option[Int],
  flagKey: Option[String],
  flagTags: Option[List[String]],
  flagTagsOperator: Option[String] // "ANY" or "ALL"
)
