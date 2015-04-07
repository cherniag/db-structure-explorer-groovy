use cn_service;

--SRV-707
insert into system (release_time_millis, version, release_name) values(unix_timestamp(), "5.18", "5.18");
