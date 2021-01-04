package sigil.repo.impl.pg

import sigil.model.Flag
import sigil.repo.FlagRepo

import scala.concurrent.Future

class FlagRepoPGImpl extends FlagRepo {
  def list = Future.successful(List[Flag]())
}
