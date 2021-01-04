package sigil.model

final case class Flag(key: String,
                      description: String,
                      createdBy: String,
                      updatedBy: String,
                      enabled: Boolean,
                      segments: Vector[Segment],
                      variants: Vector[Variant],
                      tags: Vector[Tag],
                      snapshotId: Int,
                      notes: String,
                      dataRecordsEnabled: Boolean,
                      entityType: String,
                      evaluation: String)
