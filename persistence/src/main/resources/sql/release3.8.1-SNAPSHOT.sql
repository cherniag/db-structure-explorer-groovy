 -- Final insert of the release version
insert into system (release_time_millis, version, release_name) values(unix_timestamp(now()), "3.8.1-SN", "3.8.1-SN");

-- Implement a badge on IOS Home Screen when new App content available
-- http://jira.musicqubed.com/browse/IMP-1385
alter table tb_useriPhoneDetails add column last_push_of_content_update_millis bigint(20) NOT NULL;
alter table tb_userAndroidDetails add column last_push_of_content_update_millis bigint(20) NOT NULL;