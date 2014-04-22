insert into system (release_time_millis, version, release_name) values(unix_timestamp(now()), "release5.4.X_O2_PROMO", "5.4.X_O2_PROMO");

CREATE TABLE `subscription_campaign` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `mobile` varchar(25) NOT NULL,
  `campaign_id` varchar(25),
  PRIMARY KEY (`id`),
  INDEX `mobile-campaign_id` (mobile, campaign_id)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8;

-- http://jira.musicqubed.com/browse/GO-1069
-- Introduce "promotion" service and integrate with exisiting AUTO_OPT_IN flow
INSERT INTO tb_promotions
(description         , numUsers, maxUsers, startDate , endDate   , isActive, freeWeeks, subWeeks, userGroup, type       , showPromotion, label                  , is_white_listed) VALUES
('o2Campaign3G'      , 0       , 0       , 1356342064, 1606780800, true    , 1        , 0       , 10       , 'PromoCode', false        , 'o2Campaign3G'         , false);

INSERT INTO tb_promoCode
(code                   , promotionId                                                ,  media_type) VALUES
('o2Campaign3G'         , (select i from tb_promotions where label = 'o2Campaign3G') , 'AUDIO');

INSERT INTO tb_promotions
(description         , numUsers, maxUsers, startDate , endDate   , isActive, freeWeeks, subWeeks, userGroup, type       , showPromotion, label                  , is_white_listed) VALUES
('o2Campaign4G'      , 0       , 0       , 1356342064, 1606780800, true    , 1        , 0       , 10       , 'PromoCode', false        , 'o2Campaign4G'         , false);

INSERT INTO tb_promoCode
(code                   , promotionId                                                , media_type) VALUES
('o2Campaign4G'         , (select i from tb_promotions where label = 'o2Campaign4G') , 'VIDEO_AND_AUDIO');