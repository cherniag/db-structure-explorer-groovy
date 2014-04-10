CREATE TABLE `subscription_campaign` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `mobile` varchar(25) NOT NULL,
  `promo_code_id` int(11),
  PRIMARY KEY (`id`),
  CONSTRAINT `promo_code_id-tb_promoCode_id` FOREIGN KEY (`promo_code_id`) REFERENCES `tb_promoCode` (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8;