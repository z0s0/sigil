package sigil.model

import io.circe.generic.semiauto.{deriveEncoder, deriveDecoder}
import io.circe.Encoder

object Flag {
  implicit val jsonEncoder: Encoder[Flag] = deriveEncoder
}

final case class Flag(id: Int,
                      key: String,
                      description: String,
//                      createdBy: String,
//                      updatedBy: String,
                      enabled: Boolean,
//                      segments: Option[Vector[Segment]],
//                      variants: Option[Vector[Variant]],
//                      tags: Option[Vector[Tag]],
//                      snapshotId: Int,
                      notes: Option[String]
//                      dataRecordsEnabled: Boolean,
//                      entityType: String,
//                      evaluation: String
)
