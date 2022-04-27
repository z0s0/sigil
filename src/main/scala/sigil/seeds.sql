create database sigil;
create user sigil with password 'harold';
alter user sigil superuser;

insert into namespaces (name) values ('qa');
insert into namespaces (name) values ('prod');

insert into flags (
  namespace_id,
  key,
  description,
  created_by,
  updated_by,
  enabled,
  snapshot_id,
  notes,
  data_records_enabled,
  entity_type
)
values (
  2,
  'asddfgghjj',
  'simple toggle 1',
  'me',
  'me',
  true,
  1,
  'remove after 15th of May',
  true,
  'User'
);

insert into flags (
  namespace_id,
  key,
  description,
  created_by,
  updated_by,
  enabled,
  snapshot_id,
  notes,
  data_records_enabled,
  entity_type
)
values (
  1,
  'asddfgghjfdf',
  'simple toggle 6',
  'me',
  'me',
  true,
  1,
  'remove after 20th of May',
  false,
  'User'
);

insert into variants (flag_id, key) values
 (1, 'on'),
 (1, 'off'),
 (2, 'blue'),
 (2, 'red'),
 (2, 'green');

insert into segments (flag_id, description, rank, rollout_ppm) values
 (1, 'users in the world', 1, 100000),
 (2, 'users in Europe', 1, 250000),
 (2, 'users in US', 2, 250000);

insert into constraints (segment_id, property, operator, value) values
(1, 'country', '!=', 'RU'),
(2, 'country', '==', 'EN'),
(3, 'state', '!=', 'Texas');

insert into users (email) values ('me@gmail.com');
