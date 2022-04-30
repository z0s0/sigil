package sigil.api.v1.params

import io.circe.generic.JsonCodec

@JsonCodec final case class EvalBatchParams(
  entities: Vector[EvalEntity],
  flagIds: Option[Vector[Int]],
  flagKeys: Option[Vector[String]],
  flagTags: Option[Vector[String]],
  flagTagsOperator: Option[String] // ANY OR ALL
)

@JsonCodec final case class EvalEntity(entityId: String, entityType: String, entityContext: String)
