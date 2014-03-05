CREATE TABLE `social_info` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` int(10) unsigned DEFAULT NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `social_info_user_id` FOREIGN KEY (`user_id`) REFERENCES `tb_users` (`i`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE `facebook_user_info` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `email` varchar(100) NOT NULL,
  `first_name` varchar(100),
  `surname` varchar(100),
  `location` varchar(100),
  `profile_url` varchar(200),
  `fb_id` varchar(100),
  `user_name` varchar(100),
  PRIMARY KEY (`id`),
  CONSTRAINT `facebook_user_info_id` FOREIGN KEY (`id`) REFERENCES `social_info` (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8;


CREATE TABLE `activation_emails` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `activated` bit(1) DEFAULT b'0',
  `deviceUID` varchar(255) NOT NULL,
  `email` varchar(255) NOT NULL,
  `token` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `activation_emails_email_deviceUID_token` (`email`,`deviceUID`,`token`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

ALTER TABLE `facebook_user_info` ADD CONSTRAINT `facebook_user_info_email` UNIQUE (email);

ALTER TABLE `facebook_user_info` ADD CONSTRAINT `facebook_user_info_fb_id` UNIQUE (fb_id);