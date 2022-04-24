package sigil.api.v1

import cats.data.Validated
import cats.effect.IO
import cats.implicits.toBifunctorOps
import sigil.service.NamespaceService
import sigil.api.ClientError

final class NamespaceRoutes(srv: NamespaceService) {
  private val listLogic = Docs.Namespaces.list.serverLogic { _ => srv.list }

  private val createLogic = Docs.Namespaces.create.serverLogic { params =>
    params.validate match {
      case Validated.Valid(_) => srv.create(params).map(_.leftMap(ClientError.from))

      case Validated.Invalid(e) => IO.pure(ClientError(params.leftToJson(e).toString()))
    }
  }

  val list = List(listLogic, createLogic)
}
