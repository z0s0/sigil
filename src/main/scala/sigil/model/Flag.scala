package sigil.model

import io.circe.generic.JsonCodec

@JsonCodec
final case class Flag(
  id: Int,
  key: String,
  description: String,
//                      createdBy: String,
//                      updatedBy: String,
  enabled: Boolean,
  segments: Vector[Segment] = Vector(),
//                      variants: Vector[Variant] = Vector(),
//                      tags: Option[Vector[Tag]],
//                      snapshotId: Int,
  notes: Option[String]
//                      dataRecordsEnabled: Boolean,
//                      entityType: String,
//                      evaluation: String
)
