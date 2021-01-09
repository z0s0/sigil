package sigil.model

import io.circe.Encoder
import io.circe.generic.semiauto.deriveEncoder

object Tag {
  implicit val jsonEncoder: Encoder[Tag] = deriveEncoder
}
final case class Tag(id: Int)
