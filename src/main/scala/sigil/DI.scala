package sigil
import sigil.service.{Layer => ServiceLayer}
import sigil.repo.{Layer => DatabaseLayer}
import sigil.api.{Layer => APILayer}
import sigil.cache.{Layer => CacheLayer}
import sigil.config.{Config, DBConnection}
import zio.blocking.Blocking

object DI {

  def live =
    (Config.live ++ Blocking.live) >+> DBConnection.live >>>
      DatabaseLayer.live >>>
      ServiceLayer.live
}
