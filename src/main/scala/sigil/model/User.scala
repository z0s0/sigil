package sigil.model

import io.circe.Encoder
import io.circe.generic.semiauto.deriveEncoder

object User {
  implicit val jsonEncoder: Encoder[User] = deriveEncoder
}

final case class User(id: Int, email: String)
