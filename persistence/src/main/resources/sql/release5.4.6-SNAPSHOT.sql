CREATE TABLE `google_plus_user_info` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `email` varchar(100) NOT NULL,
  `gp_id` varchar(100),
  `display_name` varchar(100),
  `picture_url` varchar(100),
  `date_of_birth` DATETIME,
  `location` varchar(100),
  `gender` varchar(10),
  `given_name` varchar(100),
  `family_name` varchar(100),
  `home_page` varchar(100),
  PRIMARY KEY (`id`),
  CONSTRAINT `google_plus_user_info_id` FOREIGN KEY (`id`) REFERENCES `social_info` (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8;