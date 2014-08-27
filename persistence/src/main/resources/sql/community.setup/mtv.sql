SET autocommit = 0;
start TRANSACTION;
/* please, set community name here, other parameters should be checked manually */
SET @new_community_name = 'mtv';
SET @new_community_full_name = 'mtv';

/* Creating new jAdmin user for new community in cn_service_admin */
USE cn_service_admin;
INSERT INTO users (username, communityURL, password, enabled) VALUES
  ('admin', @new_community_name, md5(concat('admin', '{', 'admin', '}')), TRUE);

/* Creating new community */
USE cn_service;
SET @new_community_type_id = (SELECT
                                MAX(communityTypeID)
                              FROM tb_communities);
INSERT INTO tb_communities (name, appVersion, communityTypeID, displayName, assetName, rewriteURLParameter) VALUES
  (@new_community_name, 1, (@new_community_type_id + 1), @new_community_full_name, @new_community_name,
   @new_community_name);
SET @new_community_id = (SELECT
                           id
                         FROM tb_communities
                         WHERE rewriteURLParameter = @new_community_name);

/* Creating new DRM policy */
INSERT INTO tb_drmPolicy (name, drmType, drmValue, community) VALUES
  ('Default Policy', 1, 100, @new_community_id);

/* Creating news */
INSERT INTO tb_news (name, numEntries, community, timestamp) VALUES
  (@new_community_full_name, 10, @new_community_id, UNIX_TIMESTAMP(now()));

/* Creating playlist */
INSERT INTO tb_charts (name, numTracks, genre, timestamp, numBonusTracks, type) VALUES
  ('HOT_TRACKS - MTV', 0, 1, UNIX_TIMESTAMP(now()), 0, 'HOT_TRACKS'),
  ('FIFTH_CHART - MTV', 0, 1, UNIX_TIMESTAMP(now()), 0, 'FIFTH_CHART'),
  ('HL_UK_PLAYLIST_1 - MTV', 0, 1, UNIX_TIMESTAMP(now()), 0, 'HL_UK_PLAYLIST_1'),
  ('HL_UK_PLAYLIST_2 - MTV', 0, 1, UNIX_TIMESTAMP(now()), 0, 'HL_UK_PLAYLIST_2'),
  ('HL_UK_PLAYLIST_3 - MTV', 0, 1, UNIX_TIMESTAMP(now()), 0, 'HL_UK_PLAYLIST_2'),
  ('OTHER_CHART - MTV', 0, 1, UNIX_TIMESTAMP(now()), 0, 'OTHER_CHART');
INSERT INTO community_charts (chart_id, community_id) VALUES
  ((SELECT
      i
    FROM tb_charts
    WHERE name = 'HOT_TRACKS - MTV'), @new_community_id),
  ((SELECT
      i
    FROM tb_charts
    WHERE name = 'FIFTH_CHART - MTV'), @new_community_id),
  ((SELECT
      i
    FROM tb_charts
    WHERE name = 'HL_UK_PLAYLIST_1 - MTV'), @new_community_id),
  ((SELECT
      i
    FROM tb_charts
    WHERE name = 'HL_UK_PLAYLIST_2 - MTV'), @new_community_id),
((SELECT
      i
    FROM tb_charts
    WHERE name = 'HL_UK_PLAYLIST_3 - MTV'), @new_community_id),
  ((SELECT
      i
    FROM tb_charts
    WHERE name = 'OTHER_CHART - MTV'), @new_community_id);

/* Creating usergroup */
INSERT INTO tb_userGroups (name, community, chart, news, drmPolicy) VALUES
  (@new_community_full_name, @new_community_id, (SELECT
                                                   i
                                                 FROM tb_charts
                                                 WHERE name = 'HOT_TRACKS - MTV'), (SELECT
                                                                                     i
                                                                                   FROM tb_news
                                                                                   WHERE
                                                                                     community = @new_community_id),
   (SELECT
      i
    FROM tb_drmPolicy
    WHERE community = @new_community_id));

/* Creating payment policy */
INSERT INTO tb_paymentPolicy
(communityID, subWeeks, subCost, paymentType, operator, shortCode, currencyIso, availableInStore, app_store_product_id, contract, segment, content_category, content_type, content_description, sub_merchant_id, provider, tariff, media_type, advanced_payment_seconds, after_next_sub_payment_seconds, is_default, online)
VALUES
  (@new_community_id, 1, 1, 'PAY_PAL', NULL, '', 'GBP', FALSE,
   NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'FACEBOOK', '_3G', 'AUDIO',
   0, 0, FALSE, TRUE);
INSERT INTO tb_paymentPolicy
(communityID, subWeeks, subCost, paymentType, operator, shortCode, currencyIso, availableInStore, app_store_product_id, contract, segment, content_category, content_type, content_description, sub_merchant_id, provider, tariff, media_type, advanced_payment_seconds, after_next_sub_payment_seconds, is_default, online)
VALUES
  (@new_community_id, 1, 1, 'PAY_PAL', NULL, '', 'GBP', FALSE,
   NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'GOOGLE_PLUS', '_3G', 'AUDIO',
   0, 0, FALSE, TRUE);
INSERT INTO tb_paymentPolicy
(communityID, subWeeks, subCost, paymentType, operator, shortCode, currencyIso, availableInStore, app_store_product_id, contract, segment, content_category, content_type, content_description, sub_merchant_id, provider, tariff, media_type, advanced_payment_seconds, after_next_sub_payment_seconds, is_default, online)
VALUES
  (@new_community_id, 1, 1, 'iTunesSubscription', NULL, '', 'GBP', FALSE,
   'com.musicqubed.ios.mtv.subscription.weekly.1', NULL, NULL, NULL, NULL, NULL, NULL, NULL, '_3G', 'AUDIO',
   0, 0, FALSE, TRUE);



INSERT INTO tb_promotions (description, numUsers, maxUsers, startDate, endDate, isActive, freeWeeks, subWeeks, userGroup, type, showPromotion, label, is_white_listed)
VALUES
  ('MTVPromo2weeksAudio', 0, 0, UNIX_TIMESTAMP(now()), UNIX_TIMESTAMP('2020-12-01 02:00:00'), TRUE, 26, 0, (SELECT
                                                                                                              ug.id
                                                                                                            FROM
                                                                                                              tb_userGroups ug
                                                                                                            WHERE
                                                                                                              ug.community
                                                                                                              =
                                                                                                              @new_community_id),
   'PromoCode', FALSE, 'mtv.promo.2weeks.audio', FALSE);
INSERT INTO tb_promoCode (code, promotionId) VALUES
  ('mtv.promo.2weeks.audio', (SELECT
                                p.i
                              FROM tb_promotions p
                              WHERE p.label = 'mtv.promo.2weeks.audio'));

INSERT INTO tb_chartDetail (chart, position, media, prevPosition, chgPosition, channel, info, publishTimeMillis, version, image_filename, image_title, subtitle, title, locked, defaultChart)
VALUES
  ((SELECT
      i
    FROM tb_charts
    WHERE name = 'HOT_TRACKS - MTV'), 0, NULL, NULL, NULL, NULL, NULL, UNIX_TIMESTAMP(now()), 1, NULL,
   'HOT_TRACKS - MTV', 'HOT_TRACKS - MTV', 'HOT_TRACKS - MTV', NULL, NULL);

INSERT INTO tb_chartDetail (chart, position, media, prevPosition, chgPosition, channel, info, publishTimeMillis, version, image_filename, image_title, subtitle, title, locked, defaultChart)
VALUES
  ((SELECT
      i
    FROM tb_charts
    WHERE name = 'FIFTH_CHART - MTV'), 1, NULL, NULL, NULL, NULL, NULL, UNIX_TIMESTAMP(now()), 1, NULL,
   'FIFTH_CHART - MTV', 'FIFTH_CHART - MTV', 'FIFTH_CHART - MTV', NULL, NULL);

INSERT INTO tb_chartDetail (chart, position, media, prevPosition, chgPosition, channel, info, publishTimeMillis, version, image_filename, image_title, subtitle, title, locked, defaultChart)
VALUES
  ((SELECT
      i
    FROM tb_charts
    WHERE name = 'HL_UK_PLAYLIST_1 - MTV'), 2, NULL, NULL, NULL, NULL, NULL, UNIX_TIMESTAMP(now()), 1, NULL,
   'HL_UK_PLAYLIST_1 - MTV', 'HL_UK_PLAYLIST_1 - MTV', 'HL_UK_PLAYLIST_1 - MTV', NULL, NULL);

INSERT INTO tb_chartDetail (chart, position, media, prevPosition, chgPosition, channel, info, publishTimeMillis, version, image_filename, image_title, subtitle, title, locked, defaultChart)
VALUES
  ((SELECT
      i
    FROM tb_charts
    WHERE name = 'HL_UK_PLAYLIST_2 - MTV'), 3, NULL, NULL, NULL, NULL, NULL, UNIX_TIMESTAMP(now()), 1, NULL,
   'HL_UK_PLAYLIST_2 - MTV', 'HL_UK_PLAYLIST_2 - MTV', 'HL_UK_PLAYLIST_2 - MTV', NULL, NULL);

INSERT INTO tb_chartDetail (chart, position, media, prevPosition, chgPosition, channel, info, publishTimeMillis, version, image_filename, image_title, subtitle, title, locked, defaultChart)
VALUES
  ((SELECT
      i
    FROM tb_charts
    WHERE name = 'HL_UK_PLAYLIST_3 - MTV'), 3, NULL, NULL, NULL, NULL, NULL, UNIX_TIMESTAMP(now()), 1, NULL,
   'HL_UK_PLAYLIST_3 - MTV', 'HL_UK_PLAYLIST_3 - MTV', 'HL_UK_PLAYLIST_3 - MTV', NULL, NULL);

INSERT INTO tb_chartDetail (chart, position, media, prevPosition, chgPosition, channel, info, publishTimeMillis, version, image_filename, image_title, subtitle, title, locked, defaultChart)
VALUES
  ((SELECT
      i
    FROM tb_charts
    WHERE name = 'OTHER_CHART - MTV'), 4, NULL, NULL, NULL, NULL, NULL, UNIX_TIMESTAMP(now()), 1, NULL,
   'OTHER_CHART - MTV', 'OTHER_CHART - MTV', 'OTHER_CHART - MTV', NULL, NULL);

COMMIT;