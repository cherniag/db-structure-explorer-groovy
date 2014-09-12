CREATE TABLE `client_version_messages` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `message_key` char(100) NOT NULL,
  `url` varchar(2000),
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `client_version_info` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `device_type_id` TINYINT UNSIGNED NOT NULL,
  `community_id` int(20) NOT NULL,
  `major_number` int(11) NOT NULL,
  `minor_number` int(11) NOT NULL,
  `revision_number` int(11) NOT NULL,
  `message_id` bigint(20) NOT NULL,
  `status` char(100) NOT NULL,
  `application_name` char(100) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


alter table client_version_info add CONSTRAINT `client_version_info_community_id_fk` FOREIGN KEY (`community_id`) REFERENCES `tb_communities` (`id`);
alter table client_version_info add CONSTRAINT `client_version_info_device_type_id_fk` FOREIGN KEY (`device_type_id`) REFERENCES `tb_deviceTypes` (`i`);
alter table client_version_info add CONSTRAINT `client_version_info_message_id_fk` FOREIGN KEY (`message_id`) REFERENCES `client_version_messages` (`id`);

alter table client_version_info add UNIQUE KEY `client_version_info_uk` (`community_id`,`device_type_id`,`application_name`,`status`);