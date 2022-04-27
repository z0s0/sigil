package sigil.model
import io.circe.generic.JsonCodec

@JsonCodec final case class Namespace(id: Int, name: String)
