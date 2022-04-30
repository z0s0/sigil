package sigil.api.v1.params

import io.circe.generic.JsonCodec

@JsonCodec final case class UpdateSegmentParams(
  description: Option[String],
  rolloutPpm: Option[Int]
)
