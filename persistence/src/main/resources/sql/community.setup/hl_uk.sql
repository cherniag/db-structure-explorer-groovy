/* please, set community name here, other parameters should be checked manually */
SET @new_community_name = 'hl_uk';
SET @new_community_full_name = 'HL UK';

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
  ('FOURTH_CHART FOR HL UK', 0, 1, UNIX_TIMESTAMP(now()), 0, 'FOURTH_CHART'),
  ('HOT_TRACKS FOR HL UK', 0, 1, UNIX_TIMESTAMP(now()), 0, 'HOT_TRACKS'),
  ('FIFTH_CHART FOR HL UK', 0, 1, UNIX_TIMESTAMP(now()), 0, 'FIFTH_CHART'),
  ('HL_UK_PLAYLIST_1 FOR HL U', 0, 1, UNIX_TIMESTAMP(now()), 0, 'HL_UK_PLAYLIST_1'),
  ('HL_UK_PLAYLIST_2 FOR HL U', 0, 1, UNIX_TIMESTAMP(now()), 0, 'HL_UK_PLAYLIST_2'),
  ('OTHER_CHART FOR HL UK', 0, 1, UNIX_TIMESTAMP(now()), 0, 'OTHER_CHART');
INSERT INTO community_charts (chart_id, community_id) VALUES
  ((select i from tb_charts where name = 'HOT_TRACKS FOR HL UK'), @new_community_id),
  ((select i from tb_charts where name = 'FIFTH_CHART FOR HL UK'), @new_community_id),
  ((select i from tb_charts where name = 'HL_UK_PLAYLIST_1 FOR HL U'), @new_community_id),
  ((select i from tb_charts where name = 'HL_UK_PLAYLIST_2 FOR HL U'), @new_community_id),
  ((select i from tb_charts where name = 'OTHER_CHART FOR HL UK'), @new_community_id),
  ((select i from tb_charts where name = 'FOURTH_CHART FOR HL UK'), @new_community_id);

/* Creating usergroup */
INSERT INTO tb_userGroups (name, community, chart, news, drmPolicy) VALUES
  (@new_community_full_name, @new_community_id, (select i from tb_charts where name='HOT_TRACKS FOR HL UK'), (select i from tb_news where community = @new_community_id), (select i from tb_drmPolicy where community = @new_community_id));

/* Creating payment policy */
INSERT INTO tb_paymentPolicy
(communityID      , subWeeks, subCost, paymentType         , operator, shortCode, currencyIso, availableInStore, app_store_product_id                              , contract, segment, content_category, content_type, content_description, sub_merchant_id, provider  , tariff, media_type,advanced_payment_seconds, after_next_sub_payment_seconds, is_default, online) VALUES
(@new_community_id, 1       , 1      , 'iTunesSubscription', null    , ''       , 'GBP'      , false           , 'com.musicqubed.ios.heylist.subscription.weekly.1', null    , null   , null            , null        , null               , null           , 'FACEBOOK', '_3G' , 'AUDIO'   , 0                      ,0                              ,false      , true);

/* Creating promotions, maxUsers=0 means unlimited number of users */
INSERT INTO tb_promotions (description, numUsers, maxUsers, startDate, endDate, isActive, freeWeeks, subWeeks, userGroup, type, showPromotion, label, is_white_listed) VALUES
  ('HL_UKPromo2weeksAudio', 0, 0, UNIX_TIMESTAMP(now()), UNIX_TIMESTAMP('2020-12-01 02:00:00'), true, 2, 0, (select ug.id from tb_userGroups ug where ug.community = @new_community_id), 'PromoCode', false, 'hl_uk.promo.2weeks.audio', false);
INSERT INTO tb_promoCode (code, promotionId) VALUES
  ('hl_uk.promo.2weeks.audio',(select p.i from tb_promotions p where p.label='hl_uk.promo.2weeks.audio'));


insert into tb_chartDetail (chart, position, media, prevPosition, chgPosition, channel, info, publishTimeMillis, version, image_filename, image_title, subtitle, title, locked, defaultChart) values
  ((select i from tb_charts where name = 'FOURTH_CHART FOR HL UK'),0,null,null,null,null,null,UNIX_TIMESTAMP(now()),1,null,'FOURTH_CHART FOR HL UK','FOURTH_CHART FOR HL UK','FOURTH_CHART FOR HL UK',null,null);

insert into tb_chartDetail (chart, position, media, prevPosition, chgPosition, channel, info, publishTimeMillis, version, image_filename, image_title, subtitle, title, locked, defaultChart) values
  ((select i from tb_charts where name = 'HOT_TRACKS FOR HL UK'),1,null,null,null,null,null,UNIX_TIMESTAMP(now()),1,null,'HOT_TRACKS FOR HL UK','HOT_TRACKS FOR HL UK','HOT_TRACKS FOR HL UK',null,null);

insert into tb_chartDetail (chart, position, media, prevPosition, chgPosition, channel, info, publishTimeMillis, version, image_filename, image_title, subtitle, title, locked, defaultChart) values
  ((select i from tb_charts where name = 'FIFTH_CHART FOR HL UK'),2,null,null,null,null,null,UNIX_TIMESTAMP(now()),1,null,'FIFTH_CHART FOR HL UK','FIFTH_CHART FOR HL UK','FIFTH_CHART FOR HL UK',null,null);

insert into tb_chartDetail (chart, position, media, prevPosition, chgPosition, channel, info, publishTimeMillis, version, image_filename, image_title, subtitle, title, locked, defaultChart) values
  ((select i from tb_charts where name = 'HL_UK_PLAYLIST_1 FOR HL U'),3,null,null,null,null,null,UNIX_TIMESTAMP(now()),1,null,'HL_UK_PLAYLIST_1 FOR HL U','HL_UK_PLAYLIST_1 FOR HL U','HL_UK_PLAYLIST_1 FOR HL U',null,null);

insert into tb_chartDetail (chart, position, media, prevPosition, chgPosition, channel, info, publishTimeMillis, version, image_filename, image_title, subtitle, title, locked, defaultChart) values
  ((select i from tb_charts where name = 'HL_UK_PLAYLIST_2 FOR HL U'),4,null,null,null,null,null,UNIX_TIMESTAMP(now()),1,null,'HL_UK_PLAYLIST_2 FOR HL U','HL_UK_PLAYLIST_2 FOR HL U','HL_UK_PLAYLIST_2 FOR HL U',null,null);

insert into tb_chartDetail (chart, position, media, prevPosition, chgPosition, channel, info, publishTimeMillis, version, image_filename, image_title, subtitle, title, locked, defaultChart) values
  ((select i from tb_charts where name = 'OTHER_CHART FOR HL UK'),5,null,null,null,null,null,UNIX_TIMESTAMP(now()),1,null,'OTHER_CHART FOR HL UK','OTHER_CHART FOR HL UK','OTHER_CHART FOR HL UK',null,null);

