package sigil.api.v1.params

import cats.data.Validated
import sigil.api.ApiParams
import cats.instances.list._
import cats.instances.string._

final case class CreateSegmentParams(flagId: Int, description: String, rolloutPpm: Int)
    extends ApiParams {
  def validate: Validated[List[String], String] =
    Validated
      .cond(description.nonEmpty, "ok", List("description must be present"))
      .combine(
        Validated.cond(
          rolloutPpm > 0 & rolloutPpm <= 1000000,
          "OK",
          List("rollout out of range")
        )
      )
}
