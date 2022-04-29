package sigil.api.v1

import cats.data.Validated
import cats.effect.IO
import cats.implicits.{catsSyntaxEitherId, toBifunctorOps}
import sigil.api.ClientError
import sigil.model.{Flag, Variant}
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

  private val getLogic = Docs.Flags.get.serverLogic { id =>
    srv.get(id).map {
      case Some(flag) => flag.asRight[ClientError]
      case None       => ClientError(s"flag with id ${id} not found").asLeft[Flag]
    }
  }

  private val findVariantsLogic = Docs.Variants.find.serverLogic { flagId =>
    srv.flagVariants(flagId).map {
      case Some(vars) => vars.asRight[ClientError]
      case None       => ClientError(s"flag with id ${flagId} doesn't exist").asLeft[Vector[Variant]]
    }
  }

  val list = List(listLogic, createLogic, getLogic, findVariantsLogic)
}
