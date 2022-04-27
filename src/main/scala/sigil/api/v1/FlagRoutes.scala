package sigil.api.v1

import cats.data.Validated
import cats.effect.IO
import cats.implicits.{catsSyntaxEitherId, toBifunctorOps}
import sigil.api.ClientError
import sigil.model.Flag
import sigil.service.FlagService

final class FlagRoutes(srv: FlagService) {
  private val listLogic = Docs.Flags.list.serverLogic { params =>
    srv.list(params).map(_.asRight[Unit])
  }

  private val createLogic = Docs.Flags.create.serverLogic { params =>
    params.validate match {
      case Validated.Valid(_) =>
        srv.create(params).map(_.leftMap(ClientError.from))
      case Validated.Invalid(errors) =>
        IO.pure(ClientError(params.leftToJson(errors).toString()).asLeft[Flag])
    }
  }

  val list = List(listLogic, createLogic)
}
