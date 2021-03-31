package sigil.api.v1.params

sealed trait FlagsListParams
object FlagsListParams {
  final case class Enabled(value: Boolean) extends FlagsListParams
  final case class Key(value: String) extends FlagsListParams
  final case class Limit(value: Int) extends FlagsListParams
}
