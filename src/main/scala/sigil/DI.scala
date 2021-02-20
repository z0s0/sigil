package sigil
import sigil.service.{Layer => ServiceLayer}
import sigil.repo.{Layer => DatabaseLayer}
import sigil.api.{Layer => APILayer}
import sigil.cache.{Layer => CacheLayer}
import sigil.config.{Layer => ConfigLayer}

object DI {

  def live = {
    ConfigLayer.live >>>
      (DatabaseLayer.live ++ CacheLayer.live) >>>
      ServiceLayer.live >>>
      APILayer.live
  }
}
