package sigil.model

import io.circe.generic.semiauto.{deriveEncoder, deriveDecoder}
import io.circe.Encoder

object Flag {
  implicit val jsonEncoder: Encoder[Flag] = deriveEncoder
}

final case class Flag(key: String,
                      description: String,
                      createdBy: String,
                      updatedBy: String,
                      enabled: Boolean,
                      segments: Option[Vector[Segment]],
                      variants: Option[Vector[Variant]],
                      tags: Option[Vector[Tag]],
                      snapshotId: Int,
                      notes: String,
                      dataRecordsEnabled: Boolean,
                      entityType: String,
                      evaluation: String)
