set AUTOCOMMIT=0;
START TRANSACTION;

insert into system (release_time_millis, version, release_name) values(unix_timestamp(now()), "5.9.1", "5.9.1");

alter table sz_badge_mapping add column hidden bit(1) default null;

COMMIT;