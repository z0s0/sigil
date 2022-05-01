package sigil.service

import cats.data.OptionT
import cats.effect.IO
import cats.effect.std.Random
import cats.implicits.{catsSyntaxApplicativeId, catsSyntaxEitherId}
import sigil.api.v1.params.{EvalBatchParams, EvalParams}
import sigil.model.{EvalResult, Flag}
import sigil.util.Util

import java.util.zip.CRC32

trait EvaluationService {
  def eval(params: EvalParams): IO[Either[String, EvalResult]]
  def evalBatch(params: EvalBatchParams): IO[Vector[EvalResult]]
}

object EvaluationService {
  def of(flagService: FlagService): IO[EvaluationService] = {
    for {
      random <- Random.scalaUtilRandom[IO]
      crc32 <- IO(new CRC32())
    } yield new EvaluationService {
      def eval(params: EvalParams): IO[Either[String, EvalResult]] = {
        val flag = (params.flagId, params.flagKey) match {
          case (Some(id), _)     => flagService.get(id)
          case (None, Some(key)) => IO.raiseError(new RuntimeException(""))
          case _                 => IO.raiseError(new RuntimeException("")) // impossible branch
        }

        (for {
          flagOpt <- flag
          res <- flagOpt match {
            case Some(flag) =>
              for {
                entityId <- params.entityId.fold(random.nextInt.map(_.toString))(IO.pure)
                res <- doEval(entityId, flag)
              } yield res
            case None => EvalResult.empty.pure[IO]
          }
        } yield res).map(_.asRight[String])
      }

      def evalBatch(params: EvalBatchParams): IO[Vector[EvalResult]] = ???

      private def doEval(entityId: String, flag: Flag): IO[EvalResult] = {
        val variant = Util.toPositive(Util.hash(entityId)) % flag.variants.size
        for {
          hashResult <- IO(crc32.getValue.toInt)
          variant <- IO.pure(flag.variants(hashResult % flag.variants.size))
        } yield EvalResult
          .empty
          .copy(variantId = Some(variant.id), variantAttachment = variant.attachment)
      }
    }
  }
}
