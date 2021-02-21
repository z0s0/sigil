package sigil
import sigil.service.{Layer => ServiceLayer}
import sigil.repo.{Layer => DatabaseLayer}
import sigil.api.{Layer => APILayer}
import sigil.cache.{Layer => CacheLayer}
import sigil.config.{Config, Layer => ConfigLayer}

object DI {

  def live =
    Config.live
}
