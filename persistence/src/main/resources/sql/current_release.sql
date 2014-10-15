-- begin SRV-85

SET autocommit = 0;
START TRANSACTION;

DELETE tb_chartDetail from tb_chartDetail
LEFT JOIN tb_media  ON tb_chartDetail.media = tb_media.i
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