use cn_service;

-- SRV-670
drop table if exists google_plus_user_info, facebook_user_info, social_info;

-- SRV-755
insert into system (release_time_millis, version, release_name) values(unix_timestamp(), "5.20", "5.20");
