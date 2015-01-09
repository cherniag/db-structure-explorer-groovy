insert into system (release_time_millis, version, release_name) values(unix_timestamp(), "5.12.7", "5.12.7");

-- SRV-490 - [SERVER] CR 376: MTVNZ Promotion setup for initial usage

set AUTOCOMMIT=0;
START TRANSACTION;

select @userGroupId:= c.id from tb_communities c join tb_userGroups ug on ug.community = c.id where c.name = 'mtvnz';

update tb_promotions set isActive=false where userGroup=@userGroupId;

INSERT INTO tb_promotions
(description                 , numUsers, maxUsers, startDate                                                            , endDate                                                              , isActive, freeWeeks, subWeeks, userGroup   , type       , showPromotion, label                       , is_white_listed) VALUES
  ('mtvnz.starting.promo.audio', 0       , 0       , UNIX_TIMESTAMP(CONVERT_TZ('2014-01-08 00:00:00', '+13:00', '+00:00')), UNIX_TIMESTAMP(CONVERT_TZ('2015-03-31 23:59:59', '+13:00', '+00:00')), TRUE    , 0        , 0       , @userGroupId, 'PromoCode', 0            , 'mtvnz.starting.promo.audio', FALSE),
  ('mtvnz.2weeks.promo.audio'  , 0       , 0       , UNIX_TIMESTAMP(CONVERT_TZ('2015-03-18 00:00:00', '+13:00', '+00:00')), UNIX_TIMESTAMP('2020-12-31 00:00:00')                                , TRUE    , 2        , 0       , @userGroupId, 'PromoCode', 0            , 'mtvnz.2weeks.promo.audio'  , FALSE);


INSERT INTO tb_promoCode
(code         , promotionId, media_type)
  select p.label, i          , 'AUDIO' from tb_promotions p where p.userGroup=@userGroupId and p.label in ('mtvnz.starting.promo.audio', 'mtvnz.2weeks.promo.audio');

commit;

SET AUTOCOMMIT = 1;

-- SRV-491 - [SERVER] CR 376: Reduce playlists from 8 to 6 on MTVNZ

SET AUTOCOMMIT = 0;
START TRANSACTION;

SELECT @communityId := c.id
FROM tb_communities c
WHERE c.name = 'mtvnz';

DELETE
  community_charts
FROM
  community_charts
  JOIN tb_charts
    ON community_charts.chart_id = tb_charts.i
       AND community_charts.community_id = @communityId
       AND tb_charts.name IN (
    'HL_UK_PLAYLIST_5 - MTVNZ'
  );

COMMIT;

SET AUTOCOMMIT = 1;