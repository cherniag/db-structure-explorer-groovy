-- SRV-315 - [SERVER] Setup Promotions plan for MTV1 community

set AUTOCOMMIT=0;
START TRANSACTION;

select @userGroupId:= c.id from tb_communities c join tb_userGroups ug on ug.community = c.id where c.name = 'mtv1';

update tb_promotions set isActive=false where userGroup=@userGroupId and label in ('mtv1.promo.2weeks.audio');

-- update tb_promotions p set p.endDate=FROM_UNIXTIME('2014-11-10 23:59:59') where p.userGroup=@userGroupId and p.label='mtv1.promo.13weeks.audio';

-- INSERT INTO tb_promotions
-- (description            , numUsers, maxUsers, startDate                            , endDate                              , isActive, freeWeeks, subWeeks, userGroup   , type       , showPromotion, label          , is_white_listed) VALUES
-- ('MTV1Promo12weeksAudio', 0       , 0       , UNIX_TIMESTAMP('2014-11-11 00:00:00'), UNIX_TIMESTAMP('2014-11-17 23:59:59'), true    , 12       , 0       , @userGroupId, 'PromoCode', 0            , 'MTVPromoAudio', false),
-- ('MTV1Promo11weeksAudio', 0       , 0       , UNIX_TIMESTAMP('2014-11-18 00:00:00'), UNIX_TIMESTAMP('2014-11-24 23:59:59'), true    , 11       , 0       , @userGroupId, 'PromoCode', 0            , 'MTVPromoAudio', false),
-- ('MTV1Promo10weeksAudio', 0       , 0       , UNIX_TIMESTAMP('2014-11-25 00:00:00'), UNIX_TIMESTAMP('2014-12-01 23:59;59'), true    , 10       , 0       , @userGroupId, 'PromoCode', 0            , 'MTVPromoAudio', false),
-- ('MTV1Promo9weeksAudio' , 0       , 0       , UNIX_TIMESTAMP('2014-12-02 00:00:00'), UNIX_TIMESTAMP('2014-12-08 23:59:59'), true    , 9        , 0       , @userGroupId, 'PromoCode', 0            , 'MTVPromoAudio', false),
-- ('MTV1Promo8weeksAudio' , 0       , 0       , UNIX_TIMESTAMP('2014-12-09 00:00:00'), UNIX_TIMESTAMP('2014-12-15 23:59:59'), true    , 8        , 0       , @userGroupId, 'PromoCode', 0            , 'MTVPromoAudio', false),
-- ('MTV1Promo7weeksAudio' , 0       , 0       , UNIX_TIMESTAMP('2014-12-16 00:00:00'), UNIX_TIMESTAMP('2014-12-22 23:59:59'), true    , 7        , 0       , @userGroupId, 'PromoCode', 0            , 'MTVPromoAudio', false),
-- ('MTV1Promo6weeksAudio' , 0       , 0       , UNIX_TIMESTAMP('2014-12-23 00:00:00'), UNIX_TIMESTAMP('2014-12-29 23:59:59'), true    , 6        , 0       , @userGroupId, 'PromoCode', 0            , 'MTVPromoAudio', false),
-- ('MTV1Promo5weeksAudio' , 0       , 0       , UNIX_TIMESTAMP('2014-12-30 00:00:00'), UNIX_TIMESTAMP('2015-01-05 23:59:59'), true    , 5        , 0       , @userGroupId, 'PromoCode', 0            , 'MTVPromoAudio', false),
-- ('MTV1Promo4weeksAudio' , 0       , 0       , UNIX_TIMESTAMP('2015-01-06 00:00:00'), UNIX_TIMESTAMP('2020-12-01 02:00:00'), true    , 4        , 0       , @userGroupId, 'PromoCode', 0            , 'MTVPromoAudio', false);

-- INSERT INTO tb_promoCode
-- (code                 , promotionId, media_type)
-- select 'MTVPromoAudio', i          , 'AUDIO' from tb_promotions p where p.userGroup=@userGroupId and p.label = 'MTVPromoAudio';

 -- alternative impl start
update tb_promotions set isActive=false where userGroup=@userGroupId and label in ('mtv1.promo.13weeks.audio');

INSERT INTO tb_promotions
(description                   , numUsers, maxUsers, startDate                            , endDate                              , isActive, freeWeeks, subWeeks, userGroup   , type       , showPromotion, label                         , is_white_listed) VALUES
('MTV1PromoTill2015_01_06Audio', 0       , 0       , UNIX_TIMESTAMP('2014-09-23 14:32:29'), UNIX_TIMESTAMP('2015-01-05 23:59:59'), true    , 0        , 0       , @userGroupId, 'PromoCode', 0            , 'MTV1PromoTill2015_01_06Audio', false),
('MTV1Promo4weeksAudio'        , 0       , 0       , UNIX_TIMESTAMP('2015-01-06 00:00:00'), UNIX_TIMESTAMP('2020-12-01 02:00:00'), true    , 0        , 0       , @userGroupId, 'PromoCode', 0            , 'MTVPromoAudio'               , false);

INSERT INTO tb_promoCode
(code                 , promotionId, media_type)
select 'MTVPromoAudio', i          , 'AUDIO' from tb_promotions p where p.userGroup=@userGroupId and p.label = 'MTV1PromoTill2015_01_06Audio';
select 'MTVPromoAudio', i          , 'AUDIO' from tb_promotions p where p.userGroup=@userGroupId and p.label = 'MTVPromoAudio';
-- alternative impl end

commit;