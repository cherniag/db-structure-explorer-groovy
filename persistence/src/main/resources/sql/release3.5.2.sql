-- Final insert of the release version
insert into system (release_time_millis, version, release_name) values(unix_timestamp(now()), "3.5.2-SN", "Impetuous Zebra");
