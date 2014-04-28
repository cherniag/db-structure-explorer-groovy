CREATE TABLE `google_plus_user_info` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `email` varchar(100) NOT NULL,
  `first_name` varchar(100),
  `surname` varchar(100),
  `picture` varchar(200),
  `gp_id` varchar(100),
  PRIMARY KEY (`id`),
  CONSTRAINT `google_plus_user_info_id` FOREIGN KEY (`id`) REFERENCES `social_info` (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8;
