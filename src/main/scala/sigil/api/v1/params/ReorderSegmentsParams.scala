package sigil.api.v1.params

import io.circe.generic.JsonCodec

@JsonCodec final case class ReorderSegmentsParams(ids: List[Int])
