CREATE TABLE `reactivation_user_info` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` int(10) unsigned DEFAULT NULL,
  `reactivation_request` bit(1) DEFAULT b'0',
  PRIMARY KEY (`id`),
  CONSTRAINT `reactivation_user_info_user_id` FOREIGN KEY (`user_id`) REFERENCES `tb_users` (`i`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

ALTER TABLE `reactivation_user_info` ADD CONSTRAINT `reactivation_user_info_user_id_uq` UNIQUE (user_id);


