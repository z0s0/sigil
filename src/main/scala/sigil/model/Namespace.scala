package sigil.model
import io.circe.generic.JsonCodec

@JsonCodec case class Namespace(id: Int, name: String)
