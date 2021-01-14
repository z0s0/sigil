package sigil.service.impl

import java.util.UUID

import sigil.api.v1.params.CreateFlagParams
import sigil.model.Flag
import sigil.repo.FlagRepo
import sigil.service.FlagService

import scala.concurrent.Future

class FlagServiceImpl(flagRepo: FlagRepo[Future]) extends FlagService[Future] {
  override def list: Future[Vector[Flag]] = flagRepo.list

  override def create(params: CreateFlagParams): Future[Option[Flag]] = {
    params.key match {
      case Some(_) =>
        flagRepo.create(params)
      case None =>
        flagRepo.create(params.copy(key = Some(UUID.randomUUID().toString)))
    }
  }

  override def get(id: Int): Future[Option[Flag]] = flagRepo.get(id)
}
