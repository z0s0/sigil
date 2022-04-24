package sigil.api.v1

import sigil.api.ClientError
import sigil.api.v1.params.{CreateFlagParams, CreateNamespaceParams}
import sigil.model.{Flag, Namespace}
import sttp.model.StatusCode
import sttp.tapir.docs.openapi.OpenAPIDocsInterpreter
import sttp.tapir.json.circe.jsonBody
import sttp.tapir.generic.auto._
import sttp.tapir.{AnyEndpoint, endpoint, statusCode}
import sttp.tapir.openapi.circe.yaml._

object Docs {
  object Flags {
    val list = endpoint
      .get
      .in("v1/flags")
      .out(jsonBody[Vector[Flag]])

    val create = endpoint
      .post
      .in("v1/flags")
      .in(jsonBody[CreateFlagParams])
      .out(jsonBody[Flag])
      .errorOut(jsonBody[ClientError])
      .errorOut(statusCode(StatusCode.BadRequest))
  }

  object Namespaces {
    val list = endpoint
      .get
      .in("v1/namespaces")
      .out(jsonBody[Vector[Namespace]])

    val create = endpoint
      .post
      .in("v1/namespaces")
      .in(jsonBody[CreateNamespaceParams])
      .out(jsonBody[Namespace])
      .errorOut(statusCode(StatusCode.BadRequest))
      .errorOut(jsonBody[ClientError])
  }

  object Variants {}

  object Segments {}

  val docs: List[AnyEndpoint] = List(
    Namespaces.list,
    Namespaces.create,
    Flags.list,
    Flags.create
  )

  val yaml = OpenAPIDocsInterpreter().toOpenAPI(docs, "Sigil", "1").toYaml
}
