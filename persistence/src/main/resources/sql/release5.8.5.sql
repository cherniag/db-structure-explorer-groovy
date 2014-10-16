-- http://jira.musicqubed.com/browse/SRV-289
-- [SERVER] Activate 2 weeks promotion in MTV Prod community

set AUTOCOMMIT=0;
START TRANSACTION;

insert into system (release_time_millis, version, release_name) values(unix_timestamp(now()), "5.8.5", "5.8.5");

select @userGroupId:= c.id from tb_communities c join tb_userGroups ug on ug.community = c.id where c.name = 'mtv1';

UPDATE
    tb_promotions p JOIN tb_userGroups ug
      ON p.userGroup = ug.id JOIN tb_communities c
      ON c.id = ug.community
         AND c.name = 'mtv1'
         AND p.label = 'mtv1.promo.2weeks.audio'
         AND p.freeWeeks != 0
SET
  p.isActive = true
;

-- begin SRV-291

select @mtv1_community_id:= c.id from tb_communities c where c.name = 'mtv1';

update tb_paymentPolicy
set subCost = 0.99, subWeeks = 1
where communityID = @mtv1_community_id and paymentType in ('iTunesSubscription', 'PAY_PAL');

update tb_paymentPolicy
set app_store_product_id = 'com.musicqubed.ios.mtv1.subscription.weekly.0'
where communityID = @mtv1_community_id and paymentType='iTunesSubscription';

-- end SRV-291

-- SRV-296

UPDATE
    tb_chartDetail cd JOIN tb_charts c
      ON c.i = cd.chart JOIN community_charts cc
      ON cc.chart_id = c.i JOIN tb_communities com
      ON com.id = cc.community_id
SET
  cd.POSITION = 1
WHERE
  com.name = 'demo'
  AND cd.media IS NULL
  AND c.name = 'BASIC_CHART FOR DEMO'
;

UPDATE
    tb_chartDetail cd JOIN tb_charts c
      ON c.i = cd.chart JOIN community_charts cc
      ON cc.chart_id = c.i JOIN tb_communities com
      ON com.id = cc.community_id
SET
  cd.POSITION = 2
WHERE
  com.name = 'demo'
  AND cd.media IS NULL
  AND c.name = 'HOT_TRACKS FOR DEMO'
;

UPDATE
    tb_chartDetail cd JOIN tb_charts c
      ON c.i = cd.chart JOIN community_charts cc
      ON cc.chart_id = c.i JOIN tb_communities com
      ON com.id = cc.community_id
SET
  cd.POSITION = 3
WHERE
  com.name = 'demo'
  AND cd.media IS NULL
  AND c.name = 'FIFTH_CHART FOR DEMO'
;

UPDATE
    tb_chartDetail cd JOIN tb_charts c
      ON c.i = cd.chart JOIN community_charts cc
      ON cc.chart_id = c.i JOIN tb_communities com
      ON com.id = cc.community_id
SET
  cd.POSITION = 4
WHERE
  com.name = 'demo'
  AND cd.media IS NULL
  AND c.name = 'HL_UK_PLAYLIST_1 FOR DEMO'
;

UPDATE
    tb_chartDetail cd JOIN tb_charts c
      ON c.i = cd.chart JOIN community_charts cc
      ON cc.chart_id = c.i JOIN tb_communities com
      ON com.id = cc.community_id
SET
  cd.POSITION = 5
WHERE
  com.name = 'demo'
  AND cd.media IS NULL
  AND c.name = 'HL_UK_PLAYLIST_2 FOR DEMO'
;

UPDATE
    tb_chartDetail cd JOIN tb_charts c
      ON c.i = cd.chart JOIN community_charts cc
      ON cc.chart_id = c.i JOIN tb_communities com
      ON com.id = cc.community_id
SET
  cd.POSITION = 6
WHERE
  com.name = 'demo'
  AND cd.media IS NULL
  AND c.name = 'OTHER_CHART FOR DEMO'
;

-- SRV-296

COMMIT;
