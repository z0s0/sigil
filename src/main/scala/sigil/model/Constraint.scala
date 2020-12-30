package sigil.model

final case class Constraint(segmentId: Int,
                            property: String,
                            operator: String,
                            value: String)
