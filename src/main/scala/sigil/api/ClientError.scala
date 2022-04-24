package sigil.api

import io.circe.generic.JsonCodec
import sigil.repo.{
  DbError,
  ForeignKeyViolation,
  Impossible,
  MutationError,
  NotFound,
  ReadError,
  UniquenessViolation
}

@JsonCodec final case class ClientError(reason: String)

object ClientError {
  def from(err: DbError): ClientError =
    err match {
      case e: MutationError =>
        e match {
          case UniquenessViolation(msg) => ClientError(msg)
          case ForeignKeyViolation(msg) => ClientError(msg)
          case Impossible               => ClientError("impossible error")
        }

      case e: ReadError =>
        e match {
          case NotFound(msg) => ClientError(msg)
        }
    }
}
