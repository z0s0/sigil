package sigil.api

import cats.data.Validated

abstract class ApiParams {
  def isValid: Validated[List[String], String]
}
