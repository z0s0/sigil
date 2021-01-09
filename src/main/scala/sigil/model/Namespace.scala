package sigil.model
import io.circe.generic.semiauto.deriveEncoder
import io.circe.Encoder

object Namespace {
  implicit val jsonEncoder: Encoder[Namespace] = deriveEncoder
}
final case class Namespace(id: Int, name: String)
