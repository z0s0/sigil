package sigil.repo

import sigil.api.v1.params.{CreateFlagParams, CreateVariantParams}
import sigil.model.{Flag, Variant}

trait FlagRepo[F[_]] {
  def list: F[Vector[Flag]]
  def get(id: Int): F[Option[Flag]]
  def create(params: CreateFlagParams): F[Option[Flag]]

  def createVariant(params: CreateVariantParams): F[Either[String, Variant]]
}
