package sigil.api.v1

import cats.effect.IO

final class EvaluationRoutes() {
  private val evalLogic = Docs.Evaluation.eval.serverLogic[IO] { params =>
    ???
  }

  private val evalBatchLogic = Docs.Evaluation.evalBatch.serverLogic[IO] { params =>
    ???
  }

  val list = List(evalLogic, evalBatchLogic)
}
