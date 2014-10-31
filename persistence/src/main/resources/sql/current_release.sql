-- begin SRV-89

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
-- SRV-295 - [SERVER] Allow payment policy to be configurable either by day, month or week
alter table tb_paymentPolicy add column duration bigint;
alter table tb_paymentPolicy add column duration_unit VARCHAR(255);

START TRANSACTION;

UPDATE
    tb_paymentPolicy pp JOIN tb_communities c
      ON pp.communityID = c.id
SET
  pp.duration_unit = 'MONTHS' ,
  pp.duration = 1
WHERE
  (
    c.rewriteURLParameter = 'o2'
    AND(
      pp.provider = 'NON_O2'
      OR pp.provider IS NULL
    )
    OR(
      c.rewriteURLParameter = 'vf_nz'
      AND(
        pp.provider = 'NON_VF'
        OR pp.provider IS NULL
      )
    )
  ) or (pp.paymentType='iTunesSubscription' and pp.subWeeks=4)
;

UPDATE
  tb_paymentPolicy pp
SET
  pp.duration_unit = 'WEEKS' ,
  pp.duration = subWeeks
WHERE
  pp.duration IS NULL
;

commit;

alter table tb_paymentPolicy modify column duration int unsigned not null;
alter table tb_paymentPolicy modify column duration_unit VARCHAR(255) not null;

-- alter table tb_paymentPolicy drop column subWeeks;

alter table tb_pendingPayments add column duration int unsigned;
alter table tb_pendingPayments add column duration_unit VARCHAR(255);

alter table tb_submittedPayments add column duration int unsigned;
alter table tb_submittedPayments add column duration_unit VARCHAR(255);

alter table tb_promotionpaymentpolicy add column duration int unsigned;
alter table tb_promotionpaymentpolicy add column duration_unit VARCHAR(255);

START TRANSACTION;

UPDATE
    tb_pendingPayments ppay JOIN tb_paymentdetails pd
      ON ppay.paymentDetailsId = pd.i JOIN tb_paymentpolicy pp
      ON pp.i = pd.paymentPolicyId
SET
  ppay.duration = pp.duration ,
  ppay.duration_unit = pp.duration_unit
;

UPDATE
    tb_submittedPayments sp JOIN tb_paymentdetails pd
      ON sp.paymentDetailsId = pd.i JOIN tb_paymentpolicy pp
      ON pp.i = pd.paymentPolicyId
SET
  sp.duration = pp.duration ,
  sp.duration_unit = pp.duration_unit
;

update tb_submittedPayments set duration = subWeeks, duration_unit = 'WEEKS' where duration is null;

update tb_promotionPaymentPolicy set duration = subweeks, duration_unit = 'WEEKS';

commit;

alter table tb_pendingPayments modify column duration int unsigned not null;
alter table tb_pendingPayments modify column duration_unit VARCHAR(255) not null;
alter table tb_pendingPayments change column subWeeks subWeeks int(11) NOT NULL DEFAULT 0;

alter table tb_submittedPayments modify column duration int unsigned not null;
alter table tb_submittedPayments modify column duration_unit VARCHAR(255) not null;
alter table tb_submittedPayments change column subWeeks subWeeks int(11) NOT NULL DEFAULT 0;

alter table tb_promotionPaymentPolicy modify column duration int unsigned not null;
alter table tb_promotionPaymentPolicy modify column duration_unit VARCHAR(255) not null;
alter table tb_promotionPaymentPolicy change column subWeeks subWeeks int(11) NOT NULL DEFAULT 0;