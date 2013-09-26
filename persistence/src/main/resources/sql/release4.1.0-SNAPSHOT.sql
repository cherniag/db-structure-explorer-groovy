 -- Final insert of the release version
insert into system (release_time_millis, version, release_name) values(unix_timestamp(now()), "4.1-SN", "4.1-SN");

-- Allow whitestisted MSISDNs have access to video and locked tracks
-- http://jira.musicqubed.com/browse/IMP-2311
alter table tb_promotions add column is_white_listed BIT default false;

