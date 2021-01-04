package sigil.repo

import sigil.model.Flag

import scala.concurrent.Future

trait FlagRepo {
  def list: Future[List[Flag]]
}
