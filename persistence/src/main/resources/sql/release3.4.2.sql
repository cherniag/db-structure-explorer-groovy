alter table messages modify column position integer NOT NULL;
insert into system (release_time_millis, version, release_name) values(unix_timestamp('2012-07-24 00:00:00'), "3.4.2", "Singing Eublepharis");