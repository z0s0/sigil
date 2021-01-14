package sigil.model

import io.circe.Encoder
import io.circe.generic.semiauto.deriveEncoder

object Variant {
  implicit val jsonEncoder: Encoder[Variant] = deriveEncoder
}

final case class Variant(id: Int)
