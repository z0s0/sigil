package sigil.model

import io.circe.generic.JsonCodec

@JsonCodec
final case class Segment(
  id: Int,
  description: String,
  rank: Int,
  rollOut: Int,
  constraints: List[Constraint] = List()
)
