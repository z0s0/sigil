package sigil.api

import cats.data.Validated
import io.circe.Json
import io.circe.syntax.EncoderOps

abstract class ApiParams {
  def validate: Validated[List[String], String]

  def leftToJson(errors: List[String]): Json = errors.asJson
}
