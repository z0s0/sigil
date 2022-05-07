package sigil.repo.impl.pg

import cats.effect.IO
import cats.effect.unsafe.implicits.global
import org.scalatest.freespec.AnyFreeSpec
import sigil.api.v1.params.FindFlagsParams
import sigil.repo.{FlagRepo, PG}
import support.PostgreSqlContainer

final class FlagRepoPGImplSpec extends AnyFreeSpec {

  "list" - {

    "returns empty list if no flags" in {
      PostgreSqlContainer
        .setupTransactor()
        .use { transactor =>
          val repo = FlagRepo.of(transactor, PG)

          val list = repo.list(FindFlagsParams.Empty).unsafeRunSync()
          assert(list == Vector())

          IO.unit
        }
        .unsafeRunSync()
    }
  }
}
