package sigil.service

import sigil.model.Flag

import scala.concurrent.Future

trait FlagService {
  def list: Future[List[Flag]]
}
