use cn_service;

--SRV-695
insert into system (release_time_millis, version, release_name) values(unix_timestamp(), "5.17.2", "5.17.2");
