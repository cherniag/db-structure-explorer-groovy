/* please, set community name here, other parameters should be checked manually */
SET @new_community_name = 'mq';
SET @new_community_full_name = 'MQ';

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
  ('HOT_TRACKS FOR MQ', 0, 1, UNIX_TIMESTAMP(now()), 0, 'HOT_TRACKS'),
  ('FIFTH_CHART FOR MQ', 0, 1, UNIX_TIMESTAMP(now()), 0, 'FIFTH_CHART'),
  ('MQ_PLAYLIST_1 FOR MQ', 0, 1, UNIX_TIMESTAMP(now()), 0, 'MQ_PLAYLIST_1'),
  ('MQ_PLAYLIST_2 FOR MQ', 0, 1, UNIX_TIMESTAMP(now()), 0, 'MQ_PLAYLIST_2'),
  ('OTHER_CHART FOR MQ', 0, 1, UNIX_TIMESTAMP(now()), 0, 'OTHER_CHART'),
  ('FOURTH_CHART FOR MQ', 0, 1, UNIX_TIMESTAMP(now()), 0, 'FOURTH_CHART');
INSERT INTO community_charts (chart_id, community_id) VALUES
  ((select i from tb_charts where name = 'HOT_TRACKS FOR MQ'), @new_community_id),
  ((select i from tb_charts where name = 'FIFTH_CHART FOR MQ'), @new_community_id),
  ((select i from tb_charts where name = 'MQ_PLAYLIST_1 FOR MQ'), @new_community_id),
  ((select i from tb_charts where name = 'MQ_PLAYLIST_2 FOR MQ'), @new_community_id),
  ((select i from tb_charts where name = 'OTHER_CHART FOR MQ'), @new_community_id),
  ((select i from tb_charts where name = 'FOURTH_CHART FOR MQ'), @new_community_id);

/* Creating usergroup */
INSERT INTO tb_userGroups (name, community, chart, news, drmPolicy) VALUES
  (@new_community_full_name, @new_community_id, (select i from tb_charts where name='HOT_TRACKS FOR MQ'), (select i from tb_news where community = @new_community_id), (select i from tb_drmPolicy where community = @new_community_id));

/* Creating payment policy */
INSERT INTO tb_paymentPolicy (communityID, subWeeks, subCost, paymentType, operator, shortCode, currencyIso, availableInStore, app_store_product_id, contract, segment, content_category, content_type, content_description, sub_merchant_id, provider, tariff, media_type,is_default) VALUES
  (@new_community_id, 1, 1, 'iTunesSubscription', null, '', 'GBP', false, 'com.musicqubed.ios.heylist.subscription.weekly.1', null, null, null, null, null, null, 'FACEBOOK', '_3G', 'AUDIO', false);

/* Creating promotions, maxUsers=0 means unlimited number of users */
INSERT INTO tb_promotions (description, numUsers, maxUsers, startDate, endDate, isActive, freeWeeks, subWeeks, userGroup, type, showPromotion, label, is_white_listed) VALUES
  ('MQpromo2weeksAudio', 0, 0, UNIX_TIMESTAMP(now()), UNIX_TIMESTAMP('2020-12-01 02:00:00'), true, 2, 0, (select ug.id from tb_userGroups ug where ug.community = @new_community_id), 'PromoCode', false, 'mq.promo.2weeks.audio', false);
INSERT INTO tb_promoCode (code, promotionId) VALUES
  ('mq.promo.2weeks.audio',(select p.i from tb_promotions p where p.label='mq.promo.2weeks.audio'));
