package sigil.model
/*

Key         string `gorm:"type:varchar(64);unique_index:idx_flag_key"`
	Description string `sql:"type:text"`
	CreatedBy   string
	UpdatedBy   string
	Enabled     bool
	Segments    []Segment
	Variants    []Variant
	Tags        []Tag `gorm:"many2many:flags_tags;"`
	SnapshotID  uint
	Notes       string `sql:"type:text"`

	DataRecordsEnabled bool
	EntityType         string

	FlagEvaluation FlagEvaluation `gorm:"-" json:"-"`
 */

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
