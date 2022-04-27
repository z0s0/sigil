package sigil.model

import io.circe.generic.JsonCodec

@JsonCodec
final case class Variant(id: Int, key: String, attachment: Option[String])
