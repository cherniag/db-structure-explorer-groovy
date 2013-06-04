 -- Final insert of the release version
insert into system (release_time_millis, version, release_name) values(unix_timestamp(now()), "3.8.1-SN", "3.8.1-SN");

-- [Jadmin] Add rich popup which allows deeplinking to various "anchors" in the app/mobile web
-- http://jira.musicqubed.com/browse/IMP-1542          
alter table messages add column actionType varchar(255);
alter table messages add column action varchar(255);