insert into system (release_time_millis, version, release_name) values(unix_timestamp(now()), "5.8.2", "5.8.2");

-- http://jira.musicqubed.com/browse/SRV-260
-- [SERVER] MTV1 community set up additional 2 playlists

SET autocommit = 0;
START TRANSACTION;

select @mtv1_community_id:= c.id from tb_communities c where c.name = 'mtv1';

INSERT INTO tb_charts (name, numTracks, genre, timestamp            , numBonusTracks, type) VALUES
('HL_UK_PLAYLIST_4 - MTV1' , 0        , 1    , UNIX_TIMESTAMP(now()), 0             , 'HL_UK_PLAYLIST_2'),
('HL_UK_PLAYLIST_5 - MTV1' , 0        , 1    , UNIX_TIMESTAMP(now()), 0             , 'HL_UK_PLAYLIST_2');

INSERT INTO community_charts (chart_id                             , community_id) VALUES
((SELECT i FROM tb_charts WHERE name = 'HL_UK_PLAYLIST_4 - MTV1'), @mtv1_community_id),
((SELECT i FROM tb_charts WHERE name = 'HL_UK_PLAYLIST_5 - MTV1'), @mtv1_community_id);

INSERT INTO tb_chartDetail (chart                                , position, media, prevPosition, chgPosition, channel, info, publishTimeMillis         , version, image_filename, image_title              , subtitle                 , title                    , locked, defaultChart) VALUES
((SELECT i FROM tb_charts WHERE name = 'HL_UK_PLAYLIST_4 - MTV1'), 6       , NULL , NULL        , NULL       , NULL   , NULL, UNIX_TIMESTAMP(now())*1000, 1      , NULL          , 'HL_UK_PLAYLIST_4 - MTV1', 'HL_UK_PLAYLIST_4 - MTV1', 'HL_UK_PLAYLIST_4 - MTV1', NULL  , NULL);

INSERT INTO tb_chartDetail (chart                                , position, media, prevPosition, chgPosition, channel, info, publishTimeMillis         , version, image_filename, image_title              , subtitle                 , title                    , locked, defaultChart) VALUES
((SELECT i FROM tb_charts WHERE name = 'HL_UK_PLAYLIST_5 - MTV1'), 7       , NULL , NULL        , NULL       , NULL   , NULL, UNIX_TIMESTAMP(now())*1000, 1      , NULL          , 'HL_UK_PLAYLIST_5 - MTV1', 'HL_UK_PLAYLIST_5 - MTV1', 'HL_UK_PLAYLIST_5 - MTV1', NULL  , NULL);

commit;


-- SRV-269
SET autocommit = 0;
START TRANSACTION;

select @community_id:= c.id from tb_communities c where c.name = 'demo4';

INSERT INTO tb_charts (name, numTracks, genre, timestamp            , numBonusTracks, type) VALUES
  ('HL_UK_PLAYLIST_6 FOR D4' , 0        , 1    , UNIX_TIMESTAMP(now()), 0             , 'HL_UK_PLAYLIST_2'),
  ('HL_UK_PLAYLIST_7 FOR D4' , 0        , 1    , UNIX_TIMESTAMP(now()), 0             , 'HL_UK_PLAYLIST_2');

INSERT INTO community_charts (chart_id                             , community_id) VALUES
  ((SELECT i FROM tb_charts WHERE name = 'HL_UK_PLAYLIST_6 FOR D4'), @community_id),
  ((SELECT i FROM tb_charts WHERE name = 'HL_UK_PLAYLIST_7 FOR D4'), @community_id);

INSERT INTO tb_chartDetail (chart                                , position, media, prevPosition, chgPosition, channel, info, publishTimeMillis         , version, image_filename, image_title              , subtitle                 , title                    , locked, defaultChart) VALUES
  ((SELECT i FROM tb_charts WHERE name = 'HL_UK_PLAYLIST_6 FOR D4'), 6       , NULL , NULL        , NULL       , NULL   , NULL, UNIX_TIMESTAMP(now())*1000, 1      , NULL          , 'HL_UK_PLAYLIST_6 FOR D4', 'HL_UK_PLAYLIST_6 FOR D4', 'HL_UK_PLAYLIST_6 FOR D4', NULL  , NULL);

INSERT INTO tb_chartDetail (chart                                , position, media, prevPosition, chgPosition, channel, info, publishTimeMillis         , version, image_filename, image_title              , subtitle                 , title                    , locked, defaultChart) VALUES
  ((SELECT i FROM tb_charts WHERE name = 'HL_UK_PLAYLIST_7 FOR D4'), 7       , NULL , NULL        , NULL       , NULL   , NULL, UNIX_TIMESTAMP(now())*1000, 1      , NULL          , 'HL_UK_PLAYLIST_7 FOR D4', 'HL_UK_PLAYLIST_7 FOR D4', 'HL_UK_PLAYLIST_7 FOR D4', NULL  , NULL);

commit;
-- SRV-269


-- http://jira.musicqubed.com/browse/SRV-278
-- [SERVER] Update Free Trial Length on MTV1 to 13 weeks

set AUTOCOMMIT=0;
START TRANSACTION ;

select @userGroupId:= c.id from tb_communities c join tb_userGroups ug on ug.community = c.id where c.name = 'mtv1';

UPDATE
    tb_promotions p JOIN tb_userGroups ug
      ON p.userGroup = ug.id JOIN tb_communities c
      ON c.id = ug.community
         AND c.name = 'mtv1'
         AND p.label = 'mtv1.promo.2weeks.audio'
         AND p.freeWeeks != 0
SET
  p.isActive = false
;

INSERT INTO tb_promotions
(description            , numUsers, maxUsers, startDate , endDate   , isActive, freeWeeks, subWeeks, userGroup   , type       , showPromotion, label                     , is_white_listed) VALUES
('MTV1Promo13weeksAudio', 0       , 0       , 1411479149, 1606788000, true    , 13       , 0       , @userGroupId, 'PromoCode', 0            , 'mtv1.promo.13weeks.audio', false);

INSERT INTO tb_promoCode
(code                      , promotionId     , media_type) VALUES
('mtv1.promo.13weeks.audio', LAST_INSERT_ID(), 'AUDIO');

commit;

