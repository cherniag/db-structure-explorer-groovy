SET AUTOCOMMIT = 0;
START TRANSACTION;

USE cn_service;

SET @new_community_name = 'mtvnz';
SET @new_community_full_name = 'mtvnz';

-- INSERT INTO cn_service_admin.users
-- (username, communityURL       , password                                    , enabled) VALUES
-- ('admin' , @new_community_name, md5(concat('Cha3t5N0w1', '{', 'admin', '}')), TRUE);

INSERT INTO cn_service_admin.users
(username                     , communityURL       , password                          , enabled) VALUES
('alex.donelly@musicqubed.com', @new_community_name, '4df6f371c2356bb543f47f1c3824df6a', TRUE),
('yana.z@musicqubed.com'      , @new_community_name, '3d2c7cf528d1b06ab718ee9d02200674', TRUE);

SET @new_community_type_id = (SELECT MAX(communityTypeID) FROM tb_communities);

INSERT INTO tb_communities
(name               , appVersion, communityTypeID             , displayName             , assetName          , rewriteURLParameter) VALUES
(@new_community_name, 1         , @new_community_type_id + 1  , @new_community_full_name, @new_community_name, @new_community_name);

SET @new_community_id = (SELECT id FROM tb_communities WHERE rewriteURLParameter = @new_community_name);

INSERT INTO tb_drmPolicy
(name            , drmType, drmValue, community) VALUES
('Default Policy', 1      , 100     , @new_community_id);

INSERT INTO tb_news
(name                    , numEntries, community        , timestamp) VALUES
(@new_community_full_name, 10        , @new_community_id, UNIX_TIMESTAMP());

INSERT INTO tb_charts
(name                      , numTracks, genre, timestamp       , numBonusTracks, type) VALUES
('HOT_TRACKS - MTVNZ'      , 0        , 1    , UNIX_TIMESTAMP(), 0             , 'HOT_TRACKS'),
('FIFTH_CHART - MTVNZ'     , 0        , 1    , UNIX_TIMESTAMP(), 0             , 'FIFTH_CHART'),
('HL_UK_PLAYLIST_1 - MTVNZ', 0        , 1    , UNIX_TIMESTAMP(), 0             , 'HL_UK_PLAYLIST_1'),
('HL_UK_PLAYLIST_2 - MTVNZ', 0        , 1    , UNIX_TIMESTAMP(), 0             , 'HL_UK_PLAYLIST_2'),
('HL_UK_PLAYLIST_3 - MTVNZ', 0        , 1    , UNIX_TIMESTAMP(), 0             , 'HL_UK_PLAYLIST_2'),
('OTHER_CHART - MTVNZ'     , 0        , 1    , UNIX_TIMESTAMP(), 0             , 'OTHER_CHART'),
('HL_UK_PLAYLIST_4 - MTVNZ', 0        , 1    , UNIX_TIMESTAMP(), 0             , 'HL_UK_PLAYLIST_2'),
('HL_UK_PLAYLIST_5 - MTVNZ', 0        , 1    , UNIX_TIMESTAMP(), 0             , 'HL_UK_PLAYLIST_2');

INSERT INTO community_charts
(chart_id                                                         , community_id) VALUES
((SELECT i FROM tb_charts WHERE name = 'HOT_TRACKS - MTVNZ')      , @new_community_id),
((SELECT i FROM tb_charts WHERE name = 'FIFTH_CHART - MTVNZ')     , @new_community_id),
((SELECT i FROM tb_charts WHERE name = 'HL_UK_PLAYLIST_1 - MTVNZ'), @new_community_id),
((SELECT i FROM tb_charts WHERE name = 'HL_UK_PLAYLIST_2 - MTVNZ'), @new_community_id),
((SELECT i FROM tb_charts WHERE name = 'HL_UK_PLAYLIST_3 - MTVNZ'), @new_community_id),
((SELECT i FROM tb_charts WHERE name = 'OTHER_CHART - MTVNZ')     , @new_community_id),
((SELECT i FROM tb_charts WHERE name = 'HL_UK_PLAYLIST_4 - MTVNZ'), @new_community_id),
((SELECT i FROM tb_charts WHERE name = 'HL_UK_PLAYLIST_5 - MTVNZ'), @new_community_id);

INSERT INTO tb_userGroups
(name                    , community        , chart                                                      , news                                                       , drmPolicy) VALUES
(@new_community_full_name, @new_community_id, (SELECT i FROM tb_charts WHERE name = 'HOT_TRACKS - MTVNZ'), (SELECT i FROM tb_news WHERE community = @new_community_id), (SELECT i FROM tb_drmPolicy WHERE community = @new_community_id));

INSERT INTO tb_promotions
(description            , numUsers, maxUsers, startDate       , endDate                              , isActive, freeWeeks, subWeeks, userGroup                                                                 , type       , showPromotion, label                    , is_white_listed) VALUES
('MTVNZPromo4weeksAudio', 0       , 0       , UNIX_TIMESTAMP(), UNIX_TIMESTAMP('2020-12-01 02:00:00'), TRUE    , 4        , 0       , (SELECT ug.id FROM tb_userGroups ug WHERE ug.community =@new_community_id), 'PromoCode', FALSE        , 'mtvnz.promo.4weeks.audio', FALSE);

INSERT INTO tb_promoCode
(code                     , promotionId)  VALUES
('mtvnz.promo.4weeks.audio', (SELECT p.i FROM tb_promotions p WHERE p.label = 'mtvnz.promo.4weeks.audio'));

INSERT INTO tb_chartDetail
(chart                                                            , position, media, prevPosition, chgPosition, channel, info, publishTimeMillis    , version, image_filename, image_title               , subtitle                  , title                     , locked, defaultChart) VALUES
((SELECT i FROM tb_charts WHERE name = 'HOT_TRACKS - MTVNZ')      , 1       , NULL , NULL        , NULL       , NULL   , NULL, UNIX_TIMESTAMP()*1000, 1      , NULL          , 'HOT_TRACKS - MTVNZ'      , 'HOT_TRACKS - MTVNZ'      , 'HOT_TRACKS - MTVNZ'      , NULL  , NULL),
((SELECT i FROM tb_charts WHERE name = 'FIFTH_CHART - MTVNZ')     , 2       , NULL , NULL        , NULL       , NULL   , NULL, UNIX_TIMESTAMP()*1000, 1      , NULL          , 'FIFTH_CHART - MTVNZ'     , 'FIFTH_CHART - MTVNZ'     , 'FIFTH_CHART - MTVNZ'     , NULL  , NULL),
((SELECT i FROM tb_charts WHERE name = 'HL_UK_PLAYLIST_1 - MTVNZ'), 3       , NULL , NULL        , NULL       , NULL   , NULL, UNIX_TIMESTAMP()*1000, 1      , NULL          , 'HL_UK_PLAYLIST_1 - MTVNZ', 'HL_UK_PLAYLIST_1 - MTVNZ', 'HL_UK_PLAYLIST_1 - MTVNZ', NULL  , NULL),
((SELECT i FROM tb_charts WHERE name = 'HL_UK_PLAYLIST_2 - MTVNZ'), 4       , NULL , NULL        , NULL       , NULL   , NULL, UNIX_TIMESTAMP()*1000, 1      , NULL          , 'HL_UK_PLAYLIST_2 - MTVNZ', 'HL_UK_PLAYLIST_2 - MTVNZ', 'HL_UK_PLAYLIST_2 - MTVNZ', NULL  , NULL),
((SELECT i FROM tb_charts WHERE name = 'HL_UK_PLAYLIST_3 - MTVNZ'), 5       , NULL , NULL        , NULL       , NULL   , NULL, UNIX_TIMESTAMP()*1000, 1      , NULL          , 'HL_UK_PLAYLIST_3 - MTVNZ', 'HL_UK_PLAYLIST_3 - MTVNZ', 'HL_UK_PLAYLIST_3 - MTVNZ', NULL  , NULL),
((SELECT i FROM tb_charts WHERE name = 'OTHER_CHART - MTVNZ')     , 6       , NULL , NULL        , NULL       , NULL   , NULL, UNIX_TIMESTAMP()*1000, 1      , NULL          , 'OTHER_CHART - MTVNZ'     , 'OTHER_CHART - MTVNZ'     , 'OTHER_CHART - MTVNZ'     , NULL  , NULL),
((SELECT i FROM tb_charts WHERE name = 'HL_UK_PLAYLIST_4 - MTVNZ'), 7       , NULL , NULL        , NULL       , NULL   , NULL, UNIX_TIMESTAMP()*1000, 1      , NULL          , 'HL_UK_PLAYLIST_4 - MTVNZ', 'HL_UK_PLAYLIST_4 - MTVNZ', 'HL_UK_PLAYLIST_4 - MTVNZ', NULL  , NULL),
((SELECT i FROM tb_charts WHERE name = 'HL_UK_PLAYLIST_5 - MTVNZ'), 8       , NULL , NULL        , NULL       , NULL   , NULL, UNIX_TIMESTAMP()*1000, 1      , NULL          , 'HL_UK_PLAYLIST_5 - MTVNZ', 'HL_UK_PLAYLIST_5 - MTVNZ', 'HL_UK_PLAYLIST_5 - MTVNZ', NULL  , NULL);

COMMIT;
SET AUTOCOMMIT = 1;