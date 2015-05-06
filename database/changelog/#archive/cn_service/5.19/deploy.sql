use cn_service;

-- SRV-526
CREATE TABLE social_network_info (
  id bigint(20) AUTO_INCREMENT PRIMARY KEY,
  user_id int(10) unsigned NOT NULL,
  social_network_type varchar(10) NOT NULL,
  social_network_id varchar(100) NOT NULL,
  email varchar(254) DEFAULT NULL,
  user_name varchar(100) DEFAULT NULL,
  last_name varchar(100) DEFAULT NULL,
  first_name varchar(100) DEFAULT NULL,
  date_of_birth date DEFAULT NULL,
  gender_type varchar(10) DEFAULT NULL,
  age_range_min int(11) DEFAULT NULL,
  age_range_max int(11) DEFAULT NULL,
  profile_image_url varchar(255) DEFAULT NULL,
  profile_image_silhouette bit(1) DEFAULT NULL,
  country varchar(50) DEFAULT NULL,
  city varchar(100) DEFAULT NULL
);

ALTER TABLE social_network_info ADD CONSTRAINT fk_social_network_info_tb_users foreign key (user_id) references tb_users(i);
CREATE INDEX idx_social_network_info_user_id on social_network_info (user_id);
CREATE INDEX idx_social_network_info_social_network_type on social_network_info (social_network_type);

-- reusable script. no need transactions

INSERT INTO social_network_info
(id, user_id, social_network_type, social_network_id, email, gender_type, date_of_birth, profile_image_url, last_name, first_name, country, city, user_name, age_range_min, age_range_max,
profile_image_silhouette)
  SELECT fb.id,
    si.user_id,
    'FACEBOOK',
    fb.fb_id,
    fb.email,
    fb.gender,
    CAST(fb.date_of_birth AS DATE),
    fb.profile_image_url,
    fb.surname,
    fb.first_name,
    fb.country,
    fb.city,
    fb.user_name,
    fb.age_range_min,
    fb.age_range_max,
    fb.profile_image_silhouette
  FROM facebook_user_info fb
    INNER JOIN social_info si ON fb.id = si.id
  WHERE fb.id NOT IN(SELECT id FROM social_network_info);

INSERT INTO social_network_info
(id, user_id, social_network_type, social_network_id, email, gender_type, date_of_birth, profile_image_url, last_name, first_name, country, city, user_name, age_range_min, age_range_max, profile_image_silhouette)
  SELECT gp.id,
    si.user_id,
    'GOOGLE',
    gp.gp_id,
    gp.email,
    gp.gender,
    CAST(gp.date_of_birth AS DATE),
    gp.picture_url,
    gp.family_name,
    gp.given_name,
    NULL,
    gp.location,
    gp.display_name,
    NULL,
    NULL,
    NULL
  FROM google_plus_user_info gp
    INNER JOIN social_info si ON gp.id = si.id
  WHERE gp.id NOT IN(SELECT id FROM social_network_info);


-- SRV-575
ALTER TABLE tb_paymentPolicy
  ADD COLUMN start_date_time DATETIME NOT NULL DEFAULT '1970-01-01 00:00:01',
  ADD COLUMN end_date_time DATETIME NOT NULL DEFAULT '9999-12-31 23:59:59',
  ADD COLUMN message_key VARCHAR(255);


-- SRV-687
CREATE TABLE event_log(
  id BIGINT(20) PRIMARY KEY AUTO_INCREMENT,
  traceId VARCHAR(150) NOT NULL,
  type VARCHAR(100) NOT NULL,
  data TEXT,
  create_timestamp BIGINT(13) NOT NULL
);


-- SRV-726
SET AUTOCOMMIT = 0;
START TRANSACTION;

USE cn_service;

SET @new_community_name = 'mtvit';
SET @new_community_full_name = 'mtvit';

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
('HOT_TRACKS - MTVIT' , 0        , 1    , UNIX_TIMESTAMP(), 0             , 'HOT_TRACKS'),
('FIFTH_CHART - MTVIT', 0        , 1    , UNIX_TIMESTAMP(), 0             , 'FIFTH_CHART'),
('PLAYLIST_1 - MTVIT' , 0        , 1    , UNIX_TIMESTAMP(), 0             , 'PLAYLIST_1'),
('PLAYLIST_2 - MTVIT' , 0        , 1    , UNIX_TIMESTAMP(), 0             , 'PLAYLIST_2'),
('PLAYLIST_3 - MTVIT' , 0        , 1    , UNIX_TIMESTAMP(), 0             , 'PLAYLIST_2'),
('OTHER_CHART - MTVIT', 0        , 1    , UNIX_TIMESTAMP(), 0             , 'OTHER_CHART'),
('PLAYLIST_4 - MTVIT' , 0        , 1    , UNIX_TIMESTAMP(), 0             , 'PLAYLIST_2'),
('PLAYLIST_5 - MTVIT' , 0        , 1    , UNIX_TIMESTAMP(), 0             , 'PLAYLIST_2');

INSERT INTO community_charts
(chart_id                                                    , community_id) VALUES
((SELECT i FROM tb_charts WHERE name = 'HOT_TRACKS - MTVIT') , @new_community_id),
((SELECT i FROM tb_charts WHERE name = 'FIFTH_CHART - MTVIT'), @new_community_id),
((SELECT i FROM tb_charts WHERE name = 'PLAYLIST_1 - MTVIT') , @new_community_id),
((SELECT i FROM tb_charts WHERE name = 'PLAYLIST_2 - MTVIT') , @new_community_id),
((SELECT i FROM tb_charts WHERE name = 'PLAYLIST_3 - MTVIT') , @new_community_id),
((SELECT i FROM tb_charts WHERE name = 'OTHER_CHART - MTVIT'), @new_community_id),
((SELECT i FROM tb_charts WHERE name = 'PLAYLIST_4 - MTVIT') , @new_community_id),
((SELECT i FROM tb_charts WHERE name = 'PLAYLIST_5 - MTVIT') , @new_community_id);

INSERT INTO tb_userGroups
(name                    , community        , chart                                                      , news                                                       , drmPolicy) VALUES
(@new_community_full_name, @new_community_id, (SELECT i FROM tb_charts WHERE name = 'HOT_TRACKS - MTVIT'), (SELECT i FROM tb_news WHERE community = @new_community_id), (SELECT i FROM tb_drmPolicy WHERE community = @new_community_id));


set @userGroup = (SELECT ug.id FROM tb_userGroups ug WHERE ug.community=@new_community_id);
INSERT INTO tb_promotions
(description            , duration, duration_unit, numUsers, maxUsers, startDate       , endDate,                              isActive, freeWeeks, subWeeks, userGroup, type,       showPromotion, label            , is_white_listed)
VALUES
('mtvitPromo2weeksAudio', 2,        'WEEKS'      , 0       , 0       , UNIX_TIMESTAMP(), UNIX_TIMESTAMP('2020-12-01 02:00:00'),TRUE    , 2        , 0       , @userGroup,'PromoCode',FALSE,'mtvit.2weeks.promo.audio',FALSE);

INSERT INTO tb_promoCode
(code                     , promotionId)  VALUES
('mtvit.2weeks.promo.audio', (SELECT p.i FROM tb_promotions p WHERE p.label = 'mtvit.2weeks.promo.audio'));

INSERT INTO tb_chartDetail
(chart                                                       , position, media, prevPosition, chgPosition, channel, info, publishTimeMillis    , version, image_filename, image_title        , subtitle            , title                , locked, defaultChart) VALUES
((SELECT i FROM tb_charts WHERE name = 'HOT_TRACKS - MTVIT') ,1       , NULL , NULL        , NULL       , NULL   , NULL, UNIX_TIMESTAMP()*1000, 1      , NULL          , 'HOT_TRACKS - MTVIT', 'HOT_TRACKS - MTVIT', 'HOT_TRACKS - MTVIT' ,NULL  , NULL),
((SELECT i FROM tb_charts WHERE name = 'FIFTH_CHART - MTVIT'),2       , NULL , NULL        , NULL       , NULL   , NULL, UNIX_TIMESTAMP()*1000, 1      , NULL          , 'FIFTH_CHART - MTVIT','FIFTH_CHART - MTVIT','FIFTH_CHART - MTVIT',NULL  , NULL),
((SELECT i FROM tb_charts WHERE name = 'PLAYLIST_1 - MTVIT'), 3       , NULL , NULL        , NULL       , NULL   , NULL, UNIX_TIMESTAMP()*1000, 1      , NULL          , 'PLAYLIST_1 - MTVIT', 'PLAYLIST_1 - MTVIT', 'PLAYLIST_1 - MTVIT', NULL  , NULL),
((SELECT i FROM tb_charts WHERE name = 'PLAYLIST_2 - MTVIT'), 4       , NULL , NULL        , NULL       , NULL   , NULL, UNIX_TIMESTAMP()*1000, 1      , NULL          , 'PLAYLIST_2 - MTVIT', 'PLAYLIST_2 - MTVIT', 'PLAYLIST_2 - MTVIT', NULL  , NULL),
((SELECT i FROM tb_charts WHERE name = 'PLAYLIST_3 - MTVIT'), 5       , NULL , NULL        , NULL       , NULL   , NULL, UNIX_TIMESTAMP()*1000, 1      , NULL          , 'PLAYLIST_3 - MTVIT', 'PLAYLIST_3 - MTVIT', 'PLAYLIST_3 - MTVIT', NULL  , NULL),
((SELECT i FROM tb_charts WHERE name = 'OTHER_CHART - MTVIT'),6       , NULL , NULL        , NULL       , NULL   , NULL, UNIX_TIMESTAMP()*1000, 1      , NULL          , 'OTHER_CHART - MTVIT','OTHER_CHART - MTVIT','OTHER_CHART - MTVIT',NULL  , NULL),
((SELECT i FROM tb_charts WHERE name = 'PLAYLIST_4 - MTVIT'), 7       , NULL , NULL        , NULL       , NULL   , NULL, UNIX_TIMESTAMP()*1000, 1      , NULL          , 'PLAYLIST_4 - MTVIT', 'PLAYLIST_4 - MTVIT', 'PLAYLIST_4 - MTVIT', NULL  , NULL),
((SELECT i FROM tb_charts WHERE name = 'PLAYLIST_5 - MTVIT'), 8       , NULL , NULL        , NULL       , NULL   , NULL, UNIX_TIMESTAMP()*1000, 1      , NULL          , 'PLAYLIST_5 - MTVIT', 'PLAYLIST_5 - MTVIT', 'PLAYLIST_5 - MTVIT', NULL  , NULL);


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
  (select chart_id, @chartbehaviourtemplateid, 'SUBSCRIBED' from community_charts where community_id=@new_community_id)
  union (select chart_id, @chartbehaviourtemplateid, 'FREE_TRIAL' from community_charts where community_id=@new_community_id);

set @chartbehaviourtemplateid = 1 +  @chartbehaviourtemplateid;
insert into chart_behavior (id, behavior_config_id, type, play_tracks_seconds, is_offline) values (@chartbehaviourtemplateid, @freeModelid, 'PREVIEW', 30, 0);
insert into chart_user_status_behavior (chart_id, chart_behavior_id, user_status_type)
  (select chart_id, @chartbehaviourtemplateid, 'LIMITED' from community_charts where community_id=@new_community_id);

set @chartbehaviourtemplateid = 1 +  @chartbehaviourtemplateid;
insert into chart_behavior (id, behavior_config_id, type, is_offline) values (@chartbehaviourtemplateid, @freeModelid, 'SHUFFLED', 0);

insert into community_config (community_id, behavior_config_id) values  (@new_community_id, @freeModelid);

COMMIT;
SET AUTOCOMMIT = 1;


-- SRV-571
insert into system (release_time_millis, version, release_name) values(unix_timestamp(), "5.19", "5.19");
