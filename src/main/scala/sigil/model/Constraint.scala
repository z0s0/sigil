package sigil.model

import io.circe.generic.JsonCodec

@JsonCodec final case class Constraint(
  segmentId: Int,
  property: String,
  operator: String,
  value: String
)

object Constraint {

  sealed trait Operator
  case object EQ extends Operator
  case object NEQ extends Operator
  case object LT extends Operator
  case object LTE extends Operator
  case object GT extends Operator
  case object GTE extends Operator
  case object EREG extends Operator
  case object NEREG extends Operator
  case object IN extends Operator
  case object NOTIN extends Operator
  case object CONTAINS extends Operator
  case object NOTCONTAINS extends Operator

  object Operator {
//    def apply[A, B](a: A, b: B, operator: Operator): Boolean = operator match {
//      case EQ => a == b
//      case NEQ =>a != b
//      case LT => a <= b
//      case LTE =>
//      case GT =>
//      case GTE =>
//      case EREG =>
//      case NEREG =>
//      case IN =>
//      case NOTIN =>
//      case CONTAINS =>
//      case NOTCONTAINS =>
//    }
    def fromString(str: String): Option[Operator] = str match {
      case "=="           => Some(EQ)
      case "!="           => Some(NEQ)
      case "<"            => Some(LT)
      case "<="           => Some(LTE)
      case ">"            => Some(GT)
      case ">="           => Some(GTE)
      case "=~"           => Some(EREG)
      case "!~"           => Some(NEREG)
      case "IN"           => Some(IN)
      case "NOT IN"       => Some(NOTIN)
      case "CONTAINS"     => Some(CONTAINS)
      case "NOT CONTAINS" => Some(NOTCONTAINS)
      case _              => None
    }
  }
}
