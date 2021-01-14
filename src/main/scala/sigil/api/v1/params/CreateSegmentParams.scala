package sigil.api.v1.params

import cats.data.Validated
import sigil.api.ApiParams
import cats.instances.list._
import cats.instances.string._
import io.circe.Decoder
import io.circe.generic.semiauto.deriveDecoder

object CreateSegmentParams {
  implicit val jsonDecoder: Decoder[CreateSegmentParams] = deriveDecoder
}

final case class CreateSegmentParams(flagId: Int,
                                     description: String,
                                     rolloutPpm: Int)
    extends ApiParams {
  override def isValid: Validated[List[String], String] =
    Validated
      .cond(description.length > 0, "ok", List("description must be present"))
      .combine(
        Validated.cond(
          rolloutPpm > 0 & rolloutPpm <= 1000000,
          "OK",
          List("rollout out of range")
        )
      )
}
