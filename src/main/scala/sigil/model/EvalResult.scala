package sigil.model

import io.circe.generic.JsonCodec

import java.time.Instant

@JsonCodec final case class EvalResult(
  flagId: Option[Int],
  flagKey: Option[String],
  flagSnapshotId: Option[Int],
  segmentId: Option[Int],
  timestamp: Option[Instant],
  variantId: Option[Int],
  variantKey: Option[String],
  variantAttachment: Option[String],
  evalContext: Option[String],
  evalDebugLog: Option[String]
)

object EvalResult {
  val empty: EvalResult = EvalResult(None, None, None, None, None, None, None, None, None, None)
}
