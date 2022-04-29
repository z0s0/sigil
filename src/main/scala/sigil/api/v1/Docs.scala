package sigil.api.v1

import sigil.api.ClientError
import sigil.api.v1.params.{CreateFlagParams, CreateNamespaceParams, FindFlagsParams}
import sigil.model.{Flag, Namespace, Variant}
import sttp.model.StatusCode
import sttp.tapir.EndpointInput.QueryParams
import sttp.tapir.docs.openapi.OpenAPIDocsInterpreter
import sttp.tapir.json.circe.jsonBody
import sttp.tapir.generic.auto._
import sttp.tapir._
import sttp.tapir.openapi.circe.yaml._

object Docs {
  object Flags {
    private val findParams =
      query[Option[Int]]("limit")
        .and(query[Option[Int]]("offset"))
        .and(query[Option[Boolean]]("enabled"))
        .and(query[Option[String]]("description"))
        .and(query[Option[String]]("tags"))
        .and(query[Option[String]]("descriptionLike"))
        .and(query[Option[String]]("key"))
        .and(query[Option[Boolean]]("preload"))
        .and(query[Option[Boolean]]("deleted"))
        .mapTo[FindFlagsParams]

    val list = endpoint
      .get
      .in("v1" / "flags")
      .in(findParams)
      .out(jsonBody[Vector[Flag]])

    val create = endpoint
      .post
      .in("v1" / "flags")
      .in(jsonBody[CreateFlagParams])
      .out(jsonBody[Flag])
      .errorOut(jsonBody[ClientError])
      .errorOut(statusCode(StatusCode.BadRequest))

    val get = endpoint
      .get
      .in("v1" / "flags" / path[Int])
      .out(jsonBody[Flag])
      .errorOut(jsonBody[ClientError])
      .errorOut(statusCode(StatusCode.NotFound))
  }

  object Namespaces {
    val list = endpoint
      .get
      .in("v1" / "namespaces")
      .out(jsonBody[Vector[Namespace]])

    val create = endpoint
      .post
      .in("v1" / "namespaces")
      .in(jsonBody[CreateNamespaceParams])
      .out(jsonBody[Namespace])
      .errorOut(statusCode(StatusCode.BadRequest))
      .errorOut(jsonBody[ClientError])
  }

  object Variants {
    val find = endpoint
      .get
      .in("v1" / path[Int] / "variants")
      .out(jsonBody[Vector[Variant]])
      .errorOut(statusCode(StatusCode.NotFound))
      .errorOut(jsonBody[ClientError])
  }

  object Segments {}

  val docs: List[AnyEndpoint] = List(
    Namespaces.list,
    Namespaces.create,
    Flags.list,
    Flags.create
  )

  val yaml = OpenAPIDocsInterpreter().toOpenAPI(docs, "Sigil", "1").toYaml
}
