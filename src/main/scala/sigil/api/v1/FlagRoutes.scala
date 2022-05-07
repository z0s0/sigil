package sigil.api.v1

import cats.data.Validated
import cats.effect.IO
import cats.implicits.{catsSyntaxEitherId, toBifunctorOps}
import sigil.api.ClientError
import sigil.model.{Flag, Segment, Variant}
import sigil.service.{FlagService, SegmentsService}

final class FlagRoutes(flagSrv: FlagService, segmentSrv: SegmentsService) {
  private val listLogic = Docs.Flags.list.serverLogic { params =>
    flagSrv.list(params).map(_.asRight[Unit])
  }

  private val createLogic = Docs.Flags.create.serverLogic { params =>
    params.validate match {
      case Validated.Valid(_) =>
        flagSrv.create(params).map(_.leftMap(ClientError.from))
      case Validated.Invalid(errors) =>
        IO.pure(ClientError(params.leftToJson(errors).toString()).asLeft[Flag])
    }
  }

  private val getLogic = Docs.Flags.get.serverLogic { id =>
    flagSrv.get(id).map {
      case Some(flag) => flag.asRight[ClientError]
      case None       => ClientError(s"flag with id ${id} not found").asLeft[Flag]
    }
  }

  private val findVariantsLogic = Docs.Variants.find.serverLogic { flagId =>
    flagSrv.flagVariants(flagId).map {
      case Some(vars) => vars.asRight[ClientError]
      case None       => ClientError(s"flag with id ${flagId} doesn't exist").asLeft[Vector[Variant]]
    }
  }

  private val createVariantLogic = Docs.Variants.create.serverLogic {
    case (flagId, params) =>
      flagSrv.createVariant(flagId, params).map {
        case Left(value)  => ClientError(value.toString).asLeft[Variant]
        case Right(value) => value.asRight[ClientError]
      }
  }

  private val updateVariantLogic = Docs.Variants.update.serverLogic {
    case (_, variantId, params) =>
      flagSrv.updateVariant(variantId, params).map {
        case Left(value)  => ClientError(value.toString).asLeft[Variant]
        case Right(value) => value.asRight[ClientError]
      }
  }

  private val deleteVariantLogic = Docs.Variants.delete.serverLogic[IO] {
    case (_, variantId) => ???
  }

  private val listSegments = Docs.Segments.list.serverLogic { flagId =>
    flagSrv.flagSegments(flagId).map {
      case Some(segments) => segments.asRight[ClientError]
      case None           => ClientError(s"flag with id ${flagId} not found").asLeft[Vector[Segment]]
    }

  }

  private val createSegment = Docs.Segments.create.serverLogic {
    case (flagId, params) =>
      segmentSrv.createSegment(flagId, params).map {
        case Left(err)      => ClientError(err).asLeft[Segment]
        case Right(segment) => segment.asRight[ClientError]
      }
  }

  private val reorderSegments = Docs.Segments.reorder.serverLogic {
    case (flagId, params) =>
      segmentSrv.reorder(flagId, params.ids).map {
        case Left(err) => ClientError(err).asLeft[Unit]
        case Right(_)  => ().asRight[ClientError]
      }
  }

  val list = List(
    listLogic,
    createLogic,
    getLogic,
    findVariantsLogic,
    createVariantLogic,
    updateVariantLogic,
    deleteVariantLogic,
    listSegments,
    createSegment,
    reorderSegments
  )
}
