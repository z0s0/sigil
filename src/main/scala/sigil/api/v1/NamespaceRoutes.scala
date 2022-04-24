package sigil.api.v1

import cats.data.Validated
import cats.effect.IO
import cats.implicits.{catsSyntaxEitherId, toBifunctorOps}
import sigil.service.NamespaceService
import sigil.api.ClientError
import sigil.model.Namespace

final class NamespaceRoutes(srv: NamespaceService) {
  private val listLogic =
    Docs.Namespaces.list.serverLogic { _ => srv.list.map(_.asRight[Unit]) }

  private val createLogic = Docs.Namespaces.create.serverLogic { params =>
    params.validate match {
      case Validated.Valid(_) => srv.create(params).map(_.leftMap(ClientError.from))

      case Validated.Invalid(e) =>
        IO.pure(
          ClientError("return back to it later").asLeft[Namespace]
        )
    }
  }

  val list = List(listLogic, createLogic)
}
