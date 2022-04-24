package sigil.repo

import cats.effect.IO
import sigil.api.v1.params.{CreateVariantParams, FindVariantParams, UpdateVariantParams}
import sigil.model.Variant

trait VariantRepo {
  def create(params: CreateVariantParams): IO[Either[DbError, Variant]]
  def find(params: FindVariantParams): IO[Option[Variant]]
  def update(params: UpdateVariantParams): IO[Either[DbError, Variant]]
}
