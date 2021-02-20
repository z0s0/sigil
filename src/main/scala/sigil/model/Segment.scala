package sigil.model

import io.circe.Encoder
import io.circe.generic.semiauto.deriveEncoder

object Segment {
  implicit val jsonEncoder: Encoder[Segment] = deriveEncoder
}

final case class Segment(id: Int, description: String, rank: Int, rollOut: Int)
