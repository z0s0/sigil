package sigil.service.impl

import sigil.model.Flag
import sigil.repo.FlagRepo
import sigil.service.FlagService

import scala.concurrent.Future

class FlagServiceImpl(flagRepo: FlagRepo[Future]) extends FlagService[Future] {
  override def list: Future[Vector[Flag]] = flagRepo.list

  override def get(id: Int): Future[Option[Flag]] = flagRepo.get(id)
}
