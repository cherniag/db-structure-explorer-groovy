CREATE TABLE `subscription_campaign` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `mobile` varchar(25) NOT NULL,
  `campaign_id` varchar(25),
  PRIMARY KEY (`id`),
  INDEX mobile-campaign_id (mobile, campaign_id)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8;