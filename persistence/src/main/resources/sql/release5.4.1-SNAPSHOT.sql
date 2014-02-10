CREATE TABLE `tb_abstractSocialInfo` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `userUID` int(10) unsigned DEFAULT NULL,
  `infoSource` varchar(100) NOT NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `tb_abstractSocialInfo_user` FOREIGN KEY (`userUID`) REFERENCES `tb_users` (`i`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE `tb_fbUserInfo` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `email` varchar(100) NOT NULL,
  `firstName` varchar(100),
  `surname` varchar(100),
  `profileUrl` varchar(100),
  `fbId` varchar(100),
  `userName` varchar(100),
  PRIMARY KEY (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8;
