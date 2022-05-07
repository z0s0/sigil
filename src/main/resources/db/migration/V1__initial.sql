CREATE TABLE namespaces(
  id serial primary key,
  created_at TIMESTAMP WITH TIME ZONE NOT NULL default (current_timestamp at time zone 'UTC'),
  updated_at TIMESTAMP WITH TIME ZONE NOT NULL default (current_timestamp at time zone 'UTC'),
  deleted_at TIMESTAMP WITH TIME ZONE,
  name text,

  UNIQUE(name)
);

CREATE TABLE flags(
  id serial primary key,
  namespace_id INTEGER NOT NULL REFERENCES namespaces(id),
  created_at TIMESTAMP WITH TIME ZONE NOT NULL default (current_timestamp at time zone 'UTC'),
  updated_at TIMESTAMP WITH TIME ZONE NOT NULL default (current_timestamp at time zone 'UTC'),
  deleted_at TIMESTAMP WITH TIME ZONE,
  key varchar(64) not null,
  description text,
  created_by text,
  updated_by text,
  enabled boolean not null default false,
  snapshot_id integer,
  notes text,
  data_records_enabled boolean,
  entity_type text,

  UNIQUE(key)
);
CREATE INDEX ON flags(deleted_at);

CREATE TABLE snapshots(
  id serial primary key,
  flag_id integer not null references flags(id),
  updated_by text,
  data jsonb not null
);

CREATE TABLE tags(
  id serial primary key,
  created_at TIMESTAMP WITH TIME ZONE NOT NULL default (current_timestamp at time zone 'UTC'),
  updated_at TIMESTAMP WITH TIME ZONE NOT NULL default (current_timestamp at time zone 'UTC'),
  deleted_at TIMESTAMP WITH TIME ZONE,
  value varchar(64)
);
CREATE INDEX on tags(value);
CREATE INDEX on tags(deleted_at);

CREATE TABLE flags_tags(
  flag_id integer not null references flags(id),
  tag_id integer not null references tags(id),

  primary key(flag_id, tag_id)
);

CREATE TABLE variants(
  id serial primary key,
  created_at TIMESTAMP WITH TIME ZONE NOT NULL default (current_timestamp at time zone 'UTC'),
  updated_at TIMESTAMP WITH TIME ZONE NOT NULL default (current_timestamp at time zone 'UTC'),
  deleted_at TIMESTAMP WITH TIME ZONE,
  flag_id INTEGER NOT NULL REFERENCES flags(id),
  key text NOT NULL,
  attachment text
);

CREATE TABLE segments(
  id serial primary key,
  created_at TIMESTAMP WITH TIME ZONE NOT NULL default (current_timestamp at time zone 'UTC'),
  updated_at TIMESTAMP WITH TIME ZONE NOT NULL default (current_timestamp at time zone 'UTC'),
  deleted_at TIMESTAMP WITH TIME ZONE,
  flag_id INTEGER NOT NULL REFERENCES flags(id),
  description text,
  rank integer not null default 0,
  rollout_ppm integer
);
CREATE INDEX on segments(flag_id);
CREATE INDEX on segments(deleted_at);
CREATE UNIQUE INDEX ON segments(id, rank);

CREATE TABLE distributions(
  id serial primary key,
  created_at TIMESTAMP WITH TIME ZONE NOT NULL default (current_timestamp at time zone 'UTC'),
  updated_at TIMESTAMP WITH TIME ZONE NOT NULL default (current_timestamp at time zone 'UTC'),
  deleted_at TIMESTAMP WITH TIME ZONE,
  segment_id INTEGER NOT NULL REFERENCES segments(id),
  variant_id INTEGER NOT NULL REFERENCES variants(id),
  ppm INTEGER NOT NULL,
  bitmap TEXT
);
CREATE INDEX on distributions(deleted_at);
CREATE INDEX on distributions(segment_id);
CREATE INDEX on distributions(variant_id);

CREATE TABLE constraints(
  id serial primary key,
  created_at TIMESTAMP WITH TIME ZONE NOT NULL default (current_timestamp at time zone 'UTC'),
  updated_at TIMESTAMP WITH TIME ZONE NOT NULL default (current_timestamp at time zone 'UTC'),
  deleted_at TIMESTAMP WITH TIME ZONE,
  segment_id INTEGER NOT NULL REFERENCES segments(id),
  property text,
  operator text,
  value text
);

CREATE INDEX on constraints(deleted_at);
CREATE INDEX on constraints(segment_id);

CREATE table users(
  id serial primary key,
  created_at TIMESTAMP WITH TIME ZONE NOT NULL default (current_timestamp at time zone 'UTC'),
  updated_at TIMESTAMP WITH TIME ZONE NOT NULL default (current_timestamp at time zone 'UTC'),
  deleted_at TIMESTAMP WITH TIME ZONE,
  email text not null,

  UNIQUE(email)
);

create index on users(deleted_at);

