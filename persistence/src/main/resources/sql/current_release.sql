-- begin SRV-85

DELETE tb_chartDetail from tb_chartDetail
LEFT JOIN tb_media  ON tb_chartDetail.media = tb_media.i
WHERE tb_media.trackId is null;

delete from tb_media tm
where trackId is null;

ALTER TABLE tb_media MODIFY trackId BIGINT NOT NULL;

-- end SRV-85