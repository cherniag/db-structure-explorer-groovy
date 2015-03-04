insert into system (release_time_millis, version, release_name) values(unix_timestamp(), "5.15", "5.15");

-- SRV-552
alter table `chart_user_status_behavior` drop COLUMN `is_locked`;
-- END OF SRV-552-- SRV-489 - [SERVER] Set default promotion to mtv1.2weeks.promo.audio

set AUTOCOMMIT=0;
START TRANSACTION;

select @userGroupId:= c.id from tb_communities c join tb_userGroups ug on ug.community = c.id where c.name = 'mtv1';

update tb_promotions p join tb_promoCode pC on p.i=pC.promotionId set isActive=false where userGroup=@userGroupId and pC.code='mtv1.4weeks.promo.audio';

INSERT INTO tb_promotions
(description              , numUsers, maxUsers, startDate                            , endDate                              , isActive, freeWeeks, subWeeks, userGroup   , type       , showPromotion, label                    , is_white_listed) VALUES
  ('mtv1.2weeks.promo.audio', 0       , 0       , UNIX_TIMESTAMP('2015-02-17 00:00:00'), UNIX_TIMESTAMP('2020-12-01 02:00:00'), TRUE    , 2        , 0       , @userGroupId, 'PromoCode', 0            , 'mtv1.2weeks.promo.audio', FALSE);

INSERT INTO tb_promoCode
(code         , promotionId, media_type)
  select p.label, i          , 'AUDIO' from tb_promotions p where p.userGroup=@userGroupId and p.label in ('mtv1.2weeks.promo.audio');

commit;

SET AUTOCOMMIT = 1;