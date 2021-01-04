package sigil.service.impl

import sigil.model.Flag
import sigil.repo.FlagRepo
import sigil.service.FlagService

import scala.concurrent.Future

class FlagServiceImpl(flagRepo: FlagRepo) extends FlagService {
  override def list: Future[List[Flag]] = flagRepo.list
}
