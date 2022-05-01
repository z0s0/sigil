package sigil.api.v1

import sigil.api.ClientError
import sigil.api.v1.params.{
  CreateFlagParams,
  CreateNamespaceParams,
  CreateVariantParams,
  EvalBatchParams,
  EvalParams,
  FindFlagsParams,
  UpdateFlagParams,
  UpdateSegmentParams,
  UpdateVariantParams
}
import sigil.model.{EvalResult, Flag, Namespace, Segment, Variant}
import sttp.model.StatusCode
import sttp.tapir.json.circe.jsonBody
import sttp.tapir.generic.auto._
import sttp.tapir._

object Docs {
  val health =
    endpoint
      .get
      .in("health")
      .out(stringBody)
      .errorOut(statusCode(StatusCode.ServiceUnavailable))

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

    val update = endpoint
      .put
      .in("v1" / "flags" / path[Int])
      .in(jsonBody[UpdateFlagParams])
      .out(jsonBody[Flag])
      .errorOut(statusCode(StatusCode.NotFound))
      .errorOut(jsonBody[ClientError])

    val delete =
      endpoint
        .delete
        .in("v1" / "flags" / path[Int])
        .out(statusCode(StatusCode.Ok))
        .errorOut(statusCode(StatusCode.NotFound))
        .errorOut(jsonBody[ClientError])

    val restore =
      endpoint
        .put
        .in("v1" / "flags" / path[Int] / "restore")
        .out(jsonBody[Flag])
        .errorOut(statusCode(StatusCode.NotFound))
        .errorOut(jsonBody[ClientError])

    val setEnabled =
      endpoint
        .put
        .in("v1" / "flags" / path[Int] / "enabled")
        .out(jsonBody[Flag])
        .errorOut(statusCode(StatusCode.NotFound))
        .errorOut(jsonBody[ClientError])

    val snapshots =
      endpoint
        .get
        .in("v1" / "flags" / path[Int] / "snapshots")
        .out(jsonBody[String])
        .errorOut(statusCode(StatusCode.NotFound))
        .errorOut(jsonBody[ClientError])

    val entityTypes =
      endpoint
        .get
        .in("v1" / "flags" / "entity_types")
        .out(jsonBody[Vector[String]])
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
      .in("v1" / "flags" / path[Int] / "variants")
      .out(jsonBody[Vector[Variant]])
      .errorOut(statusCode(StatusCode.NotFound))
      .errorOut(jsonBody[ClientError])

    val create = endpoint
      .post
      .in("v1" / "flags" / path[Int] / "variants")
      .in(jsonBody[CreateVariantParams])
      .out(jsonBody[Variant])
      .errorOut(statusCode(StatusCode.BadRequest))
      .errorOut(jsonBody[ClientError])

    val update = endpoint
      .put
      .in("v1" / "flags" / path[Int] / "variants" / path[Int])
      .in(jsonBody[UpdateVariantParams])
      .out(jsonBody[Variant])
      .errorOut(jsonBody[ClientError])
      .errorOut(statusCode(StatusCode.BadRequest))

    val delete = endpoint
      .delete
      .in("v1" / "flags" / path[Int] / "variants" / path[Int])
      .out(statusCode(StatusCode.Ok))
      .errorOut(statusCode(StatusCode.BadRequest))
      .errorOut(jsonBody[ClientError])
  }

  object Segments {

    val list = endpoint
      .get
      .in("v1" / "flags" / path[Int] / "segments")
      .out(jsonBody[Vector[Segment]])
      .errorOut(statusCode(StatusCode.NotFound))
      .errorOut(jsonBody[ClientError])

    val reorder =
      endpoint
        .put
        .in("v1" / "flags" / path[Int] / "segments" / "reorder")
        .in(jsonBody[List[Int]])
        .out(statusCode(StatusCode.Ok))
        .errorOut(statusCode(StatusCode.NotFound))
        .errorOut(jsonBody[ClientError])

    val update =
      endpoint
        .put
        .in("v1" / "flags" / path[Int] / "segments" / path[Int])
        .in(jsonBody[UpdateSegmentParams])
        .out(jsonBody[Segment])
        .errorOut(statusCode(StatusCode.NotFound))
        .errorOut(jsonBody[ClientError])

    val delete = endpoint
      .delete
      .in("v1" / "flags" / path[Int] / "segments" / path[Int])
      .out(statusCode(StatusCode.Ok))
      .errorOut(statusCode(StatusCode.NotFound))
      .errorOut(jsonBody[ClientError])
  }

  object Evaluation {
    val eval = endpoint
      .post
      .in(jsonBody[EvalParams])
      .in("v1" / "evaluation")
      .out(jsonBody[EvalResult])
      .errorOut(statusCode(StatusCode.BadRequest))

    val evalBatch = endpoint
      .post
      .in("v1" / "evaluation" / "batch")
      .in(jsonBody[EvalBatchParams])
      .out(jsonBody[Vector[EvalResult]])
  }

  val docs: List[AnyEndpoint] = List(
    Namespaces.list,
    Namespaces.create,
    Flags.list,
    Flags.create,
    Flags.delete,
    Flags.update,
    Flags.entityTypes,
    Flags.restore,
    Flags.setEnabled,
    Flags.snapshots,
    Variants.find,
    Variants.create,
    Variants.update,
    Variants.delete,
    Segments.list,
    Segments.update,
    Segments.delete,
    Segments.reorder,
    Evaluation.eval,
    Evaluation.evalBatch
  )

}
