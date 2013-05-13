 -- Final insert of the release version
insert into system (release_time_millis, version, release_name) values(unix_timestamp(now()), "3.9-SN", "3.9-SN");

 -- IMP-1365 O2 Tracks - Pop Up needed for too many download attempts in 24 hours
alter table user_logs add column phoneNumber char(25);
alter table user_logs add column type char(25);