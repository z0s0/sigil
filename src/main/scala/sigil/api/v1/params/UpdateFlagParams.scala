package sigil.api.v1.params

import io.circe.generic.JsonCodec

@JsonCodec final case class UpdateFlagParams(
  description: Option[String],
  dataRecordsEnabled: Option[Boolean],
  entityType: Option[String],
  enabled: Option[Boolean],
  key: Option[String],
  notes: Option[String]
)
