﻿set autocommit=0;
start transaction;

/* please, set community name here, other parameters should be checked manually */
SET @new_community_name = 'demo6';
SET @new_community_full_name = 'DEMO6';

/* Creating new jAdmin user for new community in cn_service_admin */
USE cn_service_admin;
INSERT INTO users (username, communityURL, password, enabled) VALUES
  ('admin', @new_community_name, md5(concat('admin', '{', 'admin', '}')), true);

/* Creating new community */
USE cn_service;
SET @new_community_type_id = (SELECT MAX(communityTypeID) FROM tb_communities);
INSERT INTO tb_communities (name, appVersion, communityTypeID, displayName, assetName, rewriteURLParameter) VALUES
  (@new_community_name, 1, (@new_community_type_id+1), @new_community_full_name, @new_community_name, @new_community_name);
SET @new_community_id = (SELECT id FROM tb_communities WHERE rewriteURLParameter = @new_community_name);

/* Creating new DRM policy */
INSERT INTO tb_drmPolicy (name, drmType, drmValue, community) VALUES
  ('Default Policy', 1, 100, @new_community_id);

/* Creating news */
INSERT INTO tb_news (name, numEntries, community, timestamp) VALUES
 (@new_community_full_name, 10, @new_community_id, UNIX_TIMESTAMP(now()));

/* Creating playlist */
INSERT INTO tb_charts (name, numTracks, genre, timestamp, numBonusTracks, type) VALUES
  ('FOURTH_CHART FOR D6', 10, 1, UNIX_TIMESTAMP(now()), 0, 'FOURTH_CHART'),
  ('HOT_TRACKS FOR D6', 10, 1, UNIX_TIMESTAMP(now()), 0, 'HOT_TRACKS'),
  ('FIFTH_CHART FOR D6', 10, 1, UNIX_TIMESTAMP(now()), 0, 'FIFTH_CHART'),
  ('HL_UK_PLAYLIST_1 FOR D6', 10, 1, UNIX_TIMESTAMP(now()), 0, 'HL_UK_PLAYLIST_1'),
  ('HL_UK_PLAYLIST_2 FOR D6', 10, 1, UNIX_TIMESTAMP(now()), 0, 'HL_UK_PLAYLIST_2'),
  ('OTHER_CHART FOR D6', 10, 1, UNIX_TIMESTAMP(now()), 0, 'OTHER_CHART');
INSERT INTO community_charts (chart_id, community_id) VALUES
  ((select i from tb_charts where name = 'HOT_TRACKS FOR D6'), @new_community_id),
  ((select i from tb_charts where name = 'FIFTH_CHART FOR D6'), @new_community_id),
  ((select i from tb_charts where name = 'HL_UK_PLAYLIST_1 FOR D6'), @new_community_id),
  ((select i from tb_charts where name = 'HL_UK_PLAYLIST_2 FOR D6'), @new_community_id),
  ((select i from tb_charts where name = 'OTHER_CHART FOR D6'), @new_community_id),
  ((select i from tb_charts where name = 'FOURTH_CHART FOR D6'), @new_community_id);

/* Creating usergroup */
INSERT INTO tb_userGroups (name, community, chart, news, drmPolicy) VALUES
  (@new_community_full_name, @new_community_id,
  (select i from tb_charts where name='HOT_TRACKS FOR D6'), (select i from tb_news where community = @new_community_id),
   (select i from tb_drmPolicy where community = @new_community_id));

/* Creating promotions, maxUsers=0 means unlimited number of users */
INSERT INTO tb_promotions (description, numUsers, maxUsers, startDate, endDate, isActive, freeWeeks, subWeeks, userGroup, type, showPromotion, label, is_white_listed) VALUES
  ('DEMO6Promo26weeksAudio', 0, 0, UNIX_TIMESTAMP(now()), UNIX_TIMESTAMP('2020-12-01 02:00:00'), true, 26, 0, (select ug.id from tb_userGroups ug where ug.community = @new_community_id), 'PromoCode', false, 'demo6.promo.26weeks.audio', false);
INSERT INTO tb_promoCode (code, promotionId) VALUES
  ('demo6.promo.26weeks.audio',(select p.i from tb_promotions p where p.label='demo6.promo.26weeks.audio'));


insert into tb_chartDetail (chart, position, media, prevPosition, chgPosition, channel, info, publishTimeMillis, version, image_filename, image_title, subtitle, title, locked, defaultChart) values
  ((select i from tb_charts where name = 'FOURTH_CHART FOR D6'),0,null,null,null,null,null,UNIX_TIMESTAMP(now()),1,null,'FOURTH_CHART FOR D6','FOURTH_CHART FOR D6','FOURTH_CHART FOR D6',null,null);

insert into tb_chartDetail (chart, position, media, prevPosition, chgPosition, channel, info, publishTimeMillis, version, image_filename, image_title, subtitle, title, locked, defaultChart) values
  ((select i from tb_charts where name = 'HOT_TRACKS FOR D6'),1,null,null,null,null,null,UNIX_TIMESTAMP(now()),1,null,'HOT_TRACKS FOR D6','HOT_TRACKS FOR D6','HOT_TRACKS FOR D6',null,null);

insert into tb_chartDetail (chart, position, media, prevPosition, chgPosition, channel, info, publishTimeMillis, version, image_filename, image_title, subtitle, title, locked, defaultChart) values
  ((select i from tb_charts where name = 'FIFTH_CHART FOR D6'),2,null,null,null,null,null,UNIX_TIMESTAMP(now()),1,null,'FIFTH_CHART FOR D6','FIFTH_CHART FOR D6','FIFTH_CHART FOR D6',null,null);

insert into tb_chartDetail (chart, position, media, prevPosition, chgPosition, channel, info, publishTimeMillis, version, image_filename, image_title, subtitle, title, locked, defaultChart) values
  ((select i from tb_charts where name = 'HL_UK_PLAYLIST_1 FOR D6'),3,null,null,null,null,null,UNIX_TIMESTAMP(now()),1,null,'HL_UK_PLAYLIST_1 FOR D6','HL_UK_PLAYLIST_1 FOR D6','HL_UK_PLAYLIST_1 FOR D6',null,null);

insert into tb_chartDetail (chart, position, media, prevPosition, chgPosition, channel, info, publishTimeMillis, version, image_filename, image_title, subtitle, title, locked, defaultChart) values
  ((select i from tb_charts where name = 'HL_UK_PLAYLIST_2 FOR D6'),4,null,null,null,null,null,UNIX_TIMESTAMP(now()),1,null,'HL_UK_PLAYLIST_2 FOR D6','HL_UK_PLAYLIST_2 FOR D6','HL_UK_PLAYLIST_2 FOR D6',null,null);

insert into tb_chartDetail (chart, position, media, prevPosition, chgPosition, channel, info, publishTimeMillis, version, image_filename, image_title, subtitle, title, locked, defaultChart) values
  ((select i from tb_charts where name = 'OTHER_CHART FOR D6'),5,null,null,null,null,null,UNIX_TIMESTAMP(now()),1,null,'OTHER_CHART FOR D6','OTHER_CHART FOR D6','OTHER_CHART FOR D6',null,null);

commit;