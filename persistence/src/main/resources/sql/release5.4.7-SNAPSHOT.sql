insert into system (release_time_millis, version, release_name) values(unix_timestamp(now()), "release5.4.7", "5.4.7");

-- http://jira.musicqubed.com/browse/GO-2007
-- [Jadmin] Update search logic to use Track ID under Charts section

ALTER TABLE tb_media MODIFY COLUMN label tinyint(3) unsigned;
update tb_media set label = null where label = 0;