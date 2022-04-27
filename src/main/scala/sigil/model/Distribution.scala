package sigil.model

final case class Distribution(segment: Option[Segment], variant: Option[Variant], percent: Int)

object Distribution {
  val defaultBucketsNum: Int = 1000
}
