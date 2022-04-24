package sigil.pub

sealed trait SupportedPubChannel

case object Kafka extends SupportedPubChannel

object SupportedPubChannel {
  def fromString(str: String): SupportedPubChannel = str match {
    case "kafka" => Kafka
  }
}
