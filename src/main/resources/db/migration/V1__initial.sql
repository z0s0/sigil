CREATE TABLE flags(
  id serial primary key,
  created_at TIMESTAMP WITH TIME ZONE NOT NULL default (current_timestamp at time zone 'UTC'),
  updated_at TIMESTAMP WITH TIME ZONE NOT NULL default (current_timestamp at time zone 'UTC'),
  deleted_at TIMESTAMP WITH TIME ZONE NOT NULL,
  key varchar(64),
  description text,
  created_by text,
  updated_by text,
  enabled boolean not null,
  snapshot_id integer,
  notes text,
  data_records_enabled boolean,
  entity_type text,

  UNIQUE(key)
);

CREATE INDEX ON flags(deleted_at);

