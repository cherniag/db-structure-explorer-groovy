CREATE TABLE `tb_abstractSocialInfo` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `userUID` int(10) unsigned DEFAULT NULL,
  `infoSource` varchar(100) NOT NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `tb_abstractSocialInfo_user` FOREIGN KEY (`userUID`) REFERENCES `tb_users` (`i`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE `tb_fbUserInfo` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `email` varchar(30) NOT NULL,
  `firstName` varchar(30),
  `surname` varchar(30),
  `profileUrl` varchar(200),
  `fbId` varchar(30),
  `userName` varchar(300),
  PRIMARY KEY (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8;


CREATE TABLE `activation_emails` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `activated` bit(1) DEFAULT b'0',
  `deviceUID` varchar(255) NOT NULL,
  `email` varchar(255) NOT NULL,
  `token` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `activation_emails_email_deviceUID_token` (`email`,`deviceUID`,`token`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8