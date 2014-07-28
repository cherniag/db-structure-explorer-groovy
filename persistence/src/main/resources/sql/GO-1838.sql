
-- http://jira.musicqubed.com/browse/GO-2007
-- [Jadmin] Update search logic to use Track ID under Charts section

ALTER TABLE tb_media MODIFY COLUMN label tinyint(3) unsigned;
update tb_media set label = null where label = 0;

alter table tb_media drop index `isrc`;

ALTER TABLE tb_media ADD UNIQUE trackId_idx (trackId);

update  cn_service.tb_media tb
LEFT JOIN cn_cms.Track tr on (tb.trackId = tr.id)
set tb.trackId = null
where tr.id is null and tb.trackId is not null