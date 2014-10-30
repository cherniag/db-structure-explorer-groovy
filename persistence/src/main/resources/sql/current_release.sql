alter table sz_badge_mapping add column hidden bit(1) default null;
-- SRV-315 - [SERVER] Setup Promotions plan for MTV1 community

set AUTOCOMMIT=0;
START TRANSACTION;

select @userGroupId:= c.id from tb_communities c join tb_userGroups ug on ug.community = c.id where c.name = 'mtv1';

update tb_promotions set isActive=false where userGroup=@userGroupId and label in ('mtv1.promo.2weeks.audio', 'mtv1.promo.13weeks.audio');

INSERT INTO tb_promotions
(description                   , numUsers, maxUsers, startDate                            , endDate                              , isActive, freeWeeks, subWeeks, userGroup   , type       , showPromotion, label                         , is_white_listed) VALUES
('MTV1PromoTill2015_02_06Audio', 0       , 0       , UNIX_TIMESTAMP('2014-09-23 14:32:29'), UNIX_TIMESTAMP('2015-02-05 23:59:59'), true    , 0        , 0       , @userGroupId, 'PromoCode', 0            , 'mtv1.starting.promo.audio'   , false),
('mtv1.4weeks.promo.audio'     , 0       , 0       , UNIX_TIMESTAMP('2015-01-08 00:00:00'), UNIX_TIMESTAMP('2020-12-01 02:00:00'), true    , 4        , 0       , @userGroupId, 'PromoCode', 0            , 'mtv1.4weeks.promo.audio'     , false);

INSERT INTO tb_promoCode
(code         , promotionId, media_type)
select p.label, i          , 'AUDIO' from tb_promotions p where p.userGroup=@userGroupId and p.label in ('mtv1.starting.promo.audio', 'mtv1.4weeks.promo.audio');

commit;