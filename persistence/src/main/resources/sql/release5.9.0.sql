-- http://jira.musicqubed.com/browse/SRV-289
-- [SERVER] Activate 2 weeks promotion in MTV Prod community

set AUTOCOMMIT=0;
START TRANSACTION;

insert into system (release_time_millis, version, release_name) values(unix_timestamp(now()), "5.9.0", "5.9.0");

-- begin SRV-85

DELETE tb_chartDetail from tb_chartDetail
JOIN tb_media  ON tb_chartDetail.media = tb_media.i
WHERE tb_media.trackId is null;

DELETE tb_drm from tb_drm
LEFT JOIN tb_media  ON tb_drm.media = tb_media.i
WHERE tb_media.trackId is null;

delete from tb_media
where trackId is null;

ALTER TABLE tb_media MODIFY trackId BIGINT NOT NULL;

commit;

-- end SRV-85

alter table client_version_info add column image_file_name varchar(255) default null;

-- BEGIN SRV-215
alter table `sz_update` drop index `sz_update`;

alter table `sz_update` add constraint `sz_update_updated_community` UNIQUE(`community_id`, `updated`);

-- END SRV-215

-- http://jira.musicqubed.com/browse/SRV-263
-- [JADMIN] Allow content manager to define the method of how to play tracks and playlists in Magazine Channel

alter table sz_deeplink_music_track add column player_type VARCHAR(255) not null DEFAULT 'REGULAR_PLAYER_ONLY';
alter table sz_deeplink_music_list add column player_type VARCHAR(255) not null DEFAULT 'REGULAR_PLAYER_ONLY';
COMMIT;
