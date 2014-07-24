
-- http://jira.musicqubed.com/browse/GO-2007
-- [Jadmin] Update search logic to use Track ID under Charts section

ALTER TABLE tb_media MODIFY COLUMN label tinyint(3) unsigned;
update tb_media set label = null where label = 0;

update cn_service.tb_media tbm
set tbm.trackId = (select tr.id from cn_cms.Track tr where tr.ISRC = tbm.isrc LIMIT 1)

alter table tb_media drop index `isrc`;

ALTER TABLE tb_media ADD UNIQUE trackId_idx (trackId);
