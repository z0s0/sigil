package sigil.cache

import cats.effect.IO
import cats.implicits.toTraverseOps
import sigil.api.v1.params.FindFlagsParams
import sigil.repo.FlagRepo

object Warmup {
  def apply(flagRepo: FlagRepo, cache: Cache): IO[Unit] =
    for {
      allFlags <- flagRepo.list(FindFlagsParams.Empty)
      _ <- allFlags.traverse(flag => IO(cache.putFlag(flag)))
    } yield ()
}
