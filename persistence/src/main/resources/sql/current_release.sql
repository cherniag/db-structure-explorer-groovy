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

-- SRV-295 - [SERVER] Allow payment policy to be configurable either by day, month or week
alter table tb_paymentPolicy add column duration bigint;
alter table tb_paymentPolicy add column duration_unit VARCHAR(255);

START TRANSACTION;

UPDATE
    tb_paymentPolicy pp JOIN tb_communities c
      ON pp.comunityId = c.id
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
    ) or pp.paymentType='iTunesSubscription'
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

alter table tb_paymentPolicy modify column duration bigint not null;
alter table tb_paymentPolicy modify column duration_unit VARCHAR(255) not null;

-- alter table tb_paymentPolicy drop column subWeeks;

alter table tb_pendingPayments add column duration bigint;
alter table tb_pendingPayments add column duration_unit VARCHAR(255);

alter table tb_submittedPayments add column duration bigint;
alter table tb_submittedPayments add column duration_unit VARCHAR(255);

alter table tb_promotionpaymentpolicy add column duration bigint;
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

alter table tb_pendingPayments modify column duration bigint not null;
alter table tb_pendingPayments modify column duration_unit VARCHAR(255) not null;
alter table tb_pendingPayments change column subWeeks subWeeks int(11) NOT NULL DEFAULT 0;

alter table tb_submittedPayments modify column duration bigint not null;
alter table tb_submittedPayments modify column duration_unit VARCHAR(255) not null;
alter table tb_submittedPayments change column subWeeks subWeeks int(11) NOT NULL DEFAULT 0;

alter table tb_promotionPaymentPolicy modify column duration bigint not null;
alter table tb_promotionPaymentPolicy modify column duration_unit VARCHAR(255) not null;

alter table tb_promotionPaymentPolicy drop column subWeeks;


