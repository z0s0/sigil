package sigil.service.impl

import sigil.api.v1.params.CreateFlagParams
import sigil.model.Flag
import sigil.repo.FlagRepo
import sigil.service.FlagService

import scala.concurrent.Future

class FlagServiceImpl(flagRepo: FlagRepo[Future]) extends FlagService[Future] {
  override def list: Future[Vector[Flag]] = flagRepo.list

  override def create(params: CreateFlagParams): Future[Option[Flag]] =
    flagRepo.create(params)
  override def get(id: Int): Future[Option[Flag]] = flagRepo.get(id)
}
