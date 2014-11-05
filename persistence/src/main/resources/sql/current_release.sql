-- begin SRV-294
set AUTOCOMMIT=0;
START TRANSACTION;
select @mtv1_community_id:= c.id from tb_communities c where c.name = 'mtv1';
update tb_paymentPolicy
set subcost = 4.99, duration_unit = 'MONTHS', duration=1
where communityID = @mtv1_community_id and paymentType in ('iTunesSubscription', 'PAY_PAL');

update tb_paymentPolicy
set app_store_product_id = 'com.musicqubed.ios.mtv1.subscription.monthly.0'
where communityID = @mtv1_community_id and paymentType='iTunesSubscription';
COMMIT;
--  end SRV-294-- begin SRV-89

drop table  cn_service.tb_labels;


CREATE TABLE `tb_labels` (
  `i` bigint(20) UNSIGNED  NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(255) DEFAULT NULL,
  PRIMARY KEY (`i`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

alter table tb_labels add constraint `label_name_uk` UNIQUE(`name`);

ALTER TABLE cn_service.tb_media DROP COLUMN label;

ALTER TABLE cn_service.tb_media add column label bigint(20) UNSIGNED;

ALTER TABLE cn_service.tb_media ADD FOREIGN KEY (`label`) REFERENCES cn_service.tb_labels(i);

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




-- end SRV-89
