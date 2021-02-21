package sigil.api

import sigil.api.v1.FlagRoutes
import sigil.api.v1.FlagRoutes.FlagRoutes
import sigil.service.Layer.Services
import zio.ZLayer

object Layer {
  type Routes = FlagRoutes
  val live: ZLayer[Services, Nothing, Routes] = FlagRoutes.live
}
