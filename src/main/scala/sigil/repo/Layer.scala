package sigil.repo

import sigil.config.DBConnection.DBTransactor
import sigil.repo.FlagRepo.FlagRepo
import sigil.repo.NamespaceRepo.NamespaceRepo
import zio.ZLayer

object Layer {
  type Repos = FlagRepo with NamespaceRepo
  def live: ZLayer[DBTransactor, Throwable, Repos] =
    FlagRepo.live ++ NamespaceRepo.live
}
