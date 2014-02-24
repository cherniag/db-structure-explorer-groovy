insert into system (release_time_millis, version, release_name) values(unix_timestamp(now()), "5.2.6-SN", "5.2.6-SN");

 -- Added amazonUrl column to Track table
ALTER TABLE Track ADD amazonUrl VARCHAR(255) DEFAULT NULL;