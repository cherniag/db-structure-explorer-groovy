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
alter table tb_paymentPolicy add column period_unit VARCHAR(255);
alter table tb_paymentPolicy add column duration bigint;

START TRANSACTION;

UPDATE
    tb_paymentPolicy pp JOIN tb_communities c
      ON pp.comunityId = c.id
SET
  pp.period_unit = 'MONTHS' ,
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
    )
;

UPDATE
  tb_paymentPolicy pp
SET
  pp.period_unit = 'WEEKS' ,
  pp.duration = subWeeks
WHERE
  pp.duration IS NULL
;

commit;

alter table tb_paymentPolicy modify column period_unit VARCHAR(255) not null;
alter table tb_paymentPolicy modify column duration bigint not null;

alter table tb_paymentPolicy drop column subWeeks;

alter table tb_pendingPayments add column duration_unit VARCHAR(255);
alter table tb_pendingPayments add column duration bigint;

alter table tb_submittedPayments add column period_unit VARCHAR(255);
alter table tb_submittedPayments add column duration bigint;

update tb_submittedPayments set duration = subWeeks, period_unit = 'WEEKS';

alter table tb_submittedPayments modify column period_unit VARCHAR(255) not null;
alter table tb_submittedPayments modify column duration bigint not null;