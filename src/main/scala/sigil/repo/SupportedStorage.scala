package sigil.repo

sealed trait SupportedStorage

case object PG extends SupportedStorage

object SupportedStorage {
  def fromString(str: String): SupportedStorage = str match {
    case "postgresql" => PG
  }
}
