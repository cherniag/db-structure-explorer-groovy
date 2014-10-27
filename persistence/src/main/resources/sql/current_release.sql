-- begin SRV-89

ALTER TABLE cn_service.tb_labels MODIFY name VARCHAR(255);
ALTER TABLE cn_service.tb_labels MODIFY i INT UNSIGNED;
ALTER TABLE cn_service.tb_media MODIFY label INT UNSIGNED;

INSERT INTO cn_service.tb_labels (name)
  SELECT DISTINCT
    (label)
  FROM cn_cms.Track tr
    LEFT JOIN cn_service.tb_labels tl ON (tr.label = tl.name)
  WHERE tl.name IS NULL AND tr.label IS NOT NULL;


UPDATE cn_service.tb_media tb
  JOIN cn_cms.Track tr ON (tb.trackId = tr.id)
  JOIN cn_service.tb_labels tl ON (tr.label = tl.name)
SET tb.label = tl.i;

ALTER TABLE cn_service.tb_media ADD CONSTRAINT `media_label_fk` FOREIGN KEY (`label`) REFERENCES cn_service.tb_labels (i);
-- end SRV-89
