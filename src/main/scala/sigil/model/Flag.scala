package sigil.model

import io.circe.generic.JsonCodec

@JsonCodec
final case class Flag(
  id: Int,
  key: String,
  description: String,
  createdBy: String,
  updatedBy: String,
  enabled: Boolean,
  segments: Vector[Segment],
  variants: Vector[Variant],
  tags: Vector[Tag],
  snapshotId: Int,
  notes: Option[String],
  dataRecordsEnabled: Boolean,
  entityType: String,
)
