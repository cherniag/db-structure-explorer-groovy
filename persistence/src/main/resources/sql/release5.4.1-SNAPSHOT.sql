CREATE TABLE `tb_abstractSocialInfo` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `userUID` int(10) unsigned DEFAULT NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `tb_abstractSocialInfo_user` FOREIGN KEY (`userUID`) REFERENCES `tb_users` (`i`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE `tb_fbUserInfo` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `email` varchar(100) NOT NULL,
  `firstName` varchar(100),
  `surname` varchar(100),
  `profileUrl` varchar(200),
  `fbId` varchar(100),
  `userName` varchar(100),
  PRIMARY KEY (`id`),
  CONSTRAINT `tb_abstractSocialInfo_id` FOREIGN KEY (`id`) REFERENCES `tb_abstractSocialInfo` (`id`)
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


ALTER TABLE `tb_users` MODIFY COLUMN mobile VARCHAR(50);