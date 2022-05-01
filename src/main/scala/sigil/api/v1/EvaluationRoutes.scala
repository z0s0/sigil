package sigil.api.v1

import cats.effect.IO
import cats.implicits.{catsSyntaxApplicativeId, catsSyntaxEitherId}
import sigil.service.EvaluationService

final class EvaluationRoutes(srv: EvaluationService) {
  private val evalLogic = Docs.Evaluation.eval.serverLogic[IO] { params =>
//    srv.eval(params).map(_.asRight[Unit])
    ???
  }

  private val evalBatchLogic = Docs.Evaluation.evalBatch.serverLogic[IO] { params =>
    ???
  }

  val list = List(evalLogic, evalBatchLogic)
}
