SET AUTOCOMMIT = 0;
START TRANSACTION;

USE cn_service;

SET @new_community_name = 'mtvie';
SET @new_community_full_name = 'mtvie';

INSERT INTO cn_service_admin.users
(username                     , communityURL       , password                          , enabled) VALUES
('alex.donelly@musicqubed.com', @new_community_name, '4df6f371c2356bb543f47f1c3824df6a', TRUE),
('yana.z@musicqubed.com'      , @new_community_name, '3d2c7cf528d1b06ab718ee9d02200674', TRUE);

SET @new_community_type_id = (SELECT MAX(communityTypeID) FROM tb_communities);

INSERT INTO tb_communities
(name               , appVersion, communityTypeID             , displayName             , assetName          , rewriteURLParameter, live) VALUES
(@new_community_name, 1         , @new_community_type_id + 1  , @new_community_full_name, @new_community_name, @new_community_name, 1);

SET @new_community_id = (SELECT id FROM tb_communities WHERE rewriteURLParameter = @new_community_name);

INSERT INTO tb_drmPolicy
(name            , drmType, drmValue, community) VALUES
('Default Policy', 1      , 100     , @new_community_id);

INSERT INTO tb_news
(name                    , numEntries, community        , timestamp) VALUES
(@new_community_full_name, 10        , @new_community_id, UNIX_TIMESTAMP());

INSERT INTO tb_charts
(name                 , numTracks, genre, timestamp       , numBonusTracks, type) VALUES
('HOT_TRACKS - MTVIE' , 0        , 1    , UNIX_TIMESTAMP(), 0             , 'HOT_TRACKS'),
('FIFTH_CHART - MTVIE', 0        , 1    , UNIX_TIMESTAMP(), 0             , 'FIFTH_CHART'),
('PLAYLIST_1 - MTVIE' , 0        , 1    , UNIX_TIMESTAMP(), 0             , 'PLAYLIST_1'),
('PLAYLIST_2 - MTVIE' , 0        , 1    , UNIX_TIMESTAMP(), 0             , 'PLAYLIST_2'),
('PLAYLIST_3 - MTVIE' , 0        , 1    , UNIX_TIMESTAMP(), 0             , 'PLAYLIST_2'),
('OTHER_CHART - MTVIE', 0        , 1    , UNIX_TIMESTAMP(), 0             , 'OTHER_CHART'),
('PLAYLIST_4 - MTVIE' , 0        , 1    , UNIX_TIMESTAMP(), 0             , 'PLAYLIST_2'),
('PLAYLIST_5 - MTVIE' , 0        , 1    , UNIX_TIMESTAMP(), 0             , 'PLAYLIST_2');

INSERT INTO community_charts
(chart_id                                                    , community_id) VALUES
((SELECT i FROM tb_charts WHERE name = 'HOT_TRACKS - MTVIE') , @new_community_id),
((SELECT i FROM tb_charts WHERE name = 'FIFTH_CHART - MTVIE'), @new_community_id),
((SELECT i FROM tb_charts WHERE name = 'PLAYLIST_1 - MTVIE') , @new_community_id),
((SELECT i FROM tb_charts WHERE name = 'PLAYLIST_2 - MTVIE') , @new_community_id),
((SELECT i FROM tb_charts WHERE name = 'PLAYLIST_3 - MTVIE') , @new_community_id),
((SELECT i FROM tb_charts WHERE name = 'OTHER_CHART - MTVIE'), @new_community_id),
((SELECT i FROM tb_charts WHERE name = 'PLAYLIST_4 - MTVIE') , @new_community_id),
((SELECT i FROM tb_charts WHERE name = 'PLAYLIST_5 - MTVIE') , @new_community_id);

INSERT INTO tb_userGroups
(name                    , community        , chart                                                      , news                                                       , drmPolicy) VALUES
(@new_community_full_name, @new_community_id, (SELECT i FROM tb_charts WHERE name = 'HOT_TRACKS - MTVIE'), (SELECT i FROM tb_news WHERE community = @new_community_id), (SELECT i FROM tb_drmPolicy WHERE community = @new_community_id));


set @userGroup = (SELECT ug.id FROM tb_userGroups ug WHERE ug.community=@new_community_id);
INSERT INTO tb_promotions
(description            , duration, duration_unit, numUsers, maxUsers, startDate       , endDate,                              isActive, freeWeeks, subWeeks, userGroup, type,       showPromotion, label            , is_white_listed)
VALUES
('mtviePromo2weeksAudio', 2,        'WEEKS'      , 0       , 0       , UNIX_TIMESTAMP(), UNIX_TIMESTAMP('2020-12-01 02:00:00'),TRUE    , 2        , 0       , @userGroup,'PromoCode',FALSE,'mtvie.2weeks.promo.audio',FALSE);

INSERT INTO tb_promoCode
(code                     , promotionId)  VALUES
('mtvie.2weeks.promo.audio', (SELECT p.i FROM tb_promotions p WHERE p.label = 'mtvie.2weeks.promo.audio'));

INSERT INTO tb_chartDetail
(chart                                                       , position, media, prevPosition, chgPosition, channel, info, publishTimeMillis    , version, image_filename, image_title        , subtitle            , title                , locked, defaultChart) VALUES
((SELECT i FROM tb_charts WHERE name = 'HOT_TRACKS - MTVIE') ,1       , NULL , NULL        , NULL       , NULL   , NULL, UNIX_TIMESTAMP()*1000, 1      , NULL          , 'HOT_TRACKS - MTVIE', 'HOT_TRACKS - MTVIE', 'HOT_TRACKS - MTVIE' ,NULL  , NULL),
((SELECT i FROM tb_charts WHERE name = 'FIFTH_CHART - MTVIE'),2       , NULL , NULL        , NULL       , NULL   , NULL, UNIX_TIMESTAMP()*1000, 1      , NULL          , 'FIFTH_CHART - MTVIE','FIFTH_CHART - MTVIE','FIFTH_CHART - MTVIE',NULL  , NULL),
((SELECT i FROM tb_charts WHERE name = 'PLAYLIST_1 - MTVIE'), 3       , NULL , NULL        , NULL       , NULL   , NULL, UNIX_TIMESTAMP()*1000, 1      , NULL          , 'PLAYLIST_1 - MTVIE', 'PLAYLIST_1 - MTVIE', 'PLAYLIST_1 - MTVIE', NULL  , NULL),
((SELECT i FROM tb_charts WHERE name = 'PLAYLIST_2 - MTVIE'), 4       , NULL , NULL        , NULL       , NULL   , NULL, UNIX_TIMESTAMP()*1000, 1      , NULL          , 'PLAYLIST_2 - MTVIE', 'PLAYLIST_2 - MTVIE', 'PLAYLIST_2 - MTVIE', NULL  , NULL),
((SELECT i FROM tb_charts WHERE name = 'PLAYLIST_3 - MTVIE'), 5       , NULL , NULL        , NULL       , NULL   , NULL, UNIX_TIMESTAMP()*1000, 1      , NULL          , 'PLAYLIST_3 - MTVIE', 'PLAYLIST_3 - MTVIE', 'PLAYLIST_3 - MTVIE', NULL  , NULL),
((SELECT i FROM tb_charts WHERE name = 'OTHER_CHART - MTVIE'),6       , NULL , NULL        , NULL       , NULL   , NULL, UNIX_TIMESTAMP()*1000, 1      , NULL          , 'OTHER_CHART - MTVIE','OTHER_CHART - MTVIE','OTHER_CHART - MTVIE',NULL  , NULL),
((SELECT i FROM tb_charts WHERE name = 'PLAYLIST_4 - MTVIE'), 7       , NULL , NULL        , NULL       , NULL   , NULL, UNIX_TIMESTAMP()*1000, 1      , NULL          , 'PLAYLIST_4 - MTVIE', 'PLAYLIST_4 - MTVIE', 'PLAYLIST_4 - MTVIE', NULL  , NULL),
((SELECT i FROM tb_charts WHERE name = 'PLAYLIST_5 - MTVIE'), 8       , NULL , NULL        , NULL       , NULL   , NULL, UNIX_TIMESTAMP()*1000, 1      , NULL          , 'PLAYLIST_5 - MTVIE', 'PLAYLIST_5 - MTVIE', 'PLAYLIST_5 - MTVIE', NULL  , NULL);


--
-- Chart behaviours
--
-- DEFAULT
set @modelid = 1 + (select coalesce(max(id), 0) from behavior_config);
insert into behavior_config (id, community_id, type, required_referrals) values(@modelid, @new_community_id, 'DEFAULT', 5);

insert into content_user_status_behavior (behavior_config_id, user_status_type) values (@modelid, 'FREE_TRIAL');
insert into content_user_status_behavior (behavior_config_id, user_status_type) values (@modelid, 'LIMITED');
insert into content_user_status_behavior (behavior_config_id, user_status_type, are_favorites_off) values (@modelid, 'SUBSCRIBED', 0);

set @chartbehaviourtemplateid = 1 + (select coalesce(max(id), 0) from chart_behavior);
insert into chart_behavior (id, behavior_config_id, type) values (@chartbehaviourtemplateid, @modelid, 'NORMAL');
insert into chart_user_status_behavior (chart_id, chart_behavior_id, user_status_type)
  (select chart_id, @chartbehaviourtemplateid, 'SUBSCRIBED' from community_charts where community_id=@new_community_id)
  union (select chart_id, @chartbehaviourtemplateid, 'FREE_TRIAL' from community_charts where community_id=@new_community_id);

set @chartbehaviourtemplateid = 1 +  @chartbehaviourtemplateid;
insert into chart_behavior (id, behavior_config_id, type, play_tracks_seconds) values (@chartbehaviourtemplateid, @modelid, 'PREVIEW', 30);
insert into chart_user_status_behavior (chart_id, chart_behavior_id, user_status_type)
  (select chart_id, @chartbehaviourtemplateid, 'LIMITED' from community_charts where community_id=@new_community_id);
set @chartbehaviourtemplateid = 1 +  @chartbehaviourtemplateid;
insert into chart_behavior (id, behavior_config_id, type) values (@chartbehaviourtemplateid, @modelid, 'SHUFFLED');

-- FREEMIUM
set @freeModelid = 1 + @modelid;
insert into behavior_config (id, community_id, type, required_referrals) values(@freeModelid, @new_community_id, 'FREEMIUM', 5);

insert into content_user_status_behavior (behavior_config_id, user_status_type) values (@freeModelid, 'FREE_TRIAL');
insert into content_user_status_behavior (behavior_config_id, user_status_type) values (@freeModelid, 'LIMITED');
insert into content_user_status_behavior (behavior_config_id, user_status_type, are_favorites_off) values (@freeModelid, 'SUBSCRIBED', 0);

set @chartbehaviourtemplateid = 1 + (select coalesce(max(id), 0) from chart_behavior);
insert into chart_behavior (id, behavior_config_id, type) values (@chartbehaviourtemplateid, @freeModelid, 'NORMAL');
insert into chart_user_status_behavior (chart_id, chart_behavior_id, user_status_type)
  (select chart_id, @chartbehaviourtemplateid, 'SUBSCRIBED' from community_charts where community_id=@communityid)
  union (select chart_id, @chartbehaviourtemplateid, 'FREE_TRIAL' from community_charts where community_id=@communityid);

set @chartbehaviourtemplateid = 1 +  @chartbehaviourtemplateid;
insert into chart_behavior (id, behavior_config_id, type, play_tracks_seconds, is_offline) values (@chartbehaviourtemplateid, @freeModelid, 'PREVIEW', 30, 0);
insert into chart_user_status_behavior (chart_id, chart_behavior_id, user_status_type)
  (select chart_id, @chartbehaviourtemplateid, 'LIMITED' from community_charts where community_id=@communityid);

set @chartbehaviourtemplateid = 1 +  @chartbehaviourtemplateid;
insert into chart_behavior (id, behavior_config_id, type, is_offline) values (@chartbehaviourtemplateid, @freeModelid, 'SHUFFLED', 0);

insert into community_config (community_id, behavior_config_id) values  (@new_community_id, @freeModelid);

COMMIT;
SET AUTOCOMMIT = 1;