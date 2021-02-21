package sigil.service

import sigil.repo.Layer.Repos
import sigil.service.FlagService.FlagService
import sigil.service.NamespaceService.NamespaceService
import zio.ZLayer

object Layer {
  type Services = FlagService with NamespaceService
  val live
    : ZLayer[Repos, Nothing, Services] = FlagService.live ++ NamespaceService.live
}
