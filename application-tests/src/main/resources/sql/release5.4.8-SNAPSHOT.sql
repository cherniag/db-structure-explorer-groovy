insert into system (release_time_millis, version, release_name) values(unix_timestamp(now()), "5.4.8", "5.4.8");

/**
Streamzine (Hey list) tables
 */
CREATE TABLE `sz_update` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `updated` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `sz_update` (`updated`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8;

CREATE TABLE `sz_filename_alias` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `file_name` varchar(128) unique DEFAULT NULL,
  `name_alias` varchar(128) DEFAULT NULL,
  `creation_date` date DEFAULT NULL,
  `domain` varchar(64) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
alter table sz_filename_alias add constraint `filename_alias_UK_alias_domain` UNIQUE(`name_alias`, `domain`);

CREATE TABLE `sz_block` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `cover_url` varchar(1024) DEFAULT NULL,
  `badge_url` varchar(1024) DEFAULT NULL,
  `included` bit(1) DEFAULT 0,
  `expanded` bit(1) DEFAULT 0,
  `position` int(11) NOT NULL,
  `shape_type` varchar(255) DEFAULT NULL,
  `sub_title` varchar(255) DEFAULT NULL,
  `title` varchar(255) DEFAULT NULL,
  `update_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK87938CB5B8F0F59F` (`update_id`),
  UNIQUE KEY `update_id-position` (`update_id`, `position`),
  CONSTRAINT `FK87938CB5B8F0F59F` FOREIGN KEY (`update_id`) REFERENCES `sz_update` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `sz_block_access_policy` (
  `id` bigint(20) NOT NULL,
  `permission` varchar(255) DEFAULT NULL,
  `block_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK9C645263F9D2C175` (`block_id`),
  CONSTRAINT `FK9C645263F9D2C175` FOREIGN KEY (`block_id`) REFERENCES `sz_block` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `sz_granted_to_types` (
  `access_policy_id` bigint(20) NOT NULL,
  `granted_to` varchar(255) DEFAULT NULL,
  KEY `FK2E376D91B2FE69AC` (`access_policy_id`),
  CONSTRAINT `FK2E376D91B2FE69AC` FOREIGN KEY (`access_policy_id`) REFERENCES `sz_block_access_policy` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `sz_deeplink_music_list` (
  `id` bigint(20) NOT NULL,
  `content_type` varchar(255) DEFAULT NULL,
  `block_id` bigint(20) DEFAULT NULL,
  `chart_type` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK4F7DA0D4F9D2C1751d6abf99` (`block_id`),
  CONSTRAINT `FK4F7DA0D4F9D2C1751d6abf99` FOREIGN KEY (`block_id`) REFERENCES `sz_block` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `sz_deeplink_music_track` (
  `id` bigint(20) NOT NULL,
  `content_type` varchar(255) DEFAULT NULL,
  `block_id` bigint(20) DEFAULT NULL,
  `media_id` int(10) unsigned DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK9061C1B0C358E0B7` (`media_id`),
  KEY `FK4F7DA0D4F9D2C1759061c1b0` (`block_id`),
  CONSTRAINT `FK4F7DA0D4F9D2C1759061c1b0` FOREIGN KEY (`block_id`) REFERENCES `sz_block` (`id`),
  CONSTRAINT `FK9061C1B0C358E0B7` FOREIGN KEY (`media_id`) REFERENCES `tb_media` (`i`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `sz_deeplink_news_list` (
  `id` bigint(20) NOT NULL,
  `content_type` varchar(255) DEFAULT NULL,
  `block_id` bigint(20) DEFAULT NULL,
  `publish_date` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK4F7DA0D4F9D2C175a906cd49` (`block_id`),
  CONSTRAINT `FK4F7DA0D4F9D2C175a906cd49` FOREIGN KEY (`block_id`) REFERENCES `sz_block` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `sz_deeplink_news_story` (
  `id` bigint(20) NOT NULL,
  `content_type` varchar(255) DEFAULT NULL,
  `block_id` bigint(20) DEFAULT NULL,
  `news_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK783A71AA4BEA504B` (`news_id`),
  KEY `FK4F7DA0D4F9D2C175783a71aa` (`block_id`),
  CONSTRAINT `FK4F7DA0D4F9D2C175783a71aa` FOREIGN KEY (`block_id`) REFERENCES `sz_block` (`id`),
  CONSTRAINT `FK783A71AA4BEA504B` FOREIGN KEY (`news_id`) REFERENCES `messages` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `sz_deeplink_promotional` (
  `id` bigint(20) NOT NULL,
  `content_type` varchar(255) DEFAULT NULL,
  `block_id` bigint(20) DEFAULT NULL,
  `link_type` varchar(255) DEFAULT NULL,
  `url` varchar(2048) DEFAULT NULL,
  `action` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK4F7DA0D4F9D2C175252df9cc` (`block_id`),
  CONSTRAINT `FK4F7DA0D4F9D2C175252df9cc` FOREIGN KEY (`block_id`) REFERENCES `sz_block` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `sz_deeplink_man_compilation` (
  `id` bigint(20) NOT NULL,
  `content_type` varchar(255) DEFAULT NULL,
  `block_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `SZ-MAN_COMP-BLOCK_ID_FK` FOREIGN KEY (`block_id`) REFERENCES `sz_block` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `sz_man_compilation_items` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `man_compilation_id` bigint(20) NOT NULL,
  `media_id` int(10) unsigned NOT NULL,
  `position` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `SZ-MAN_COMP_ITEM-MAN_COMP-FK` FOREIGN KEY (`man_compilation_id`) REFERENCES `sz_deeplink_man_compilation` (`id`),
  CONSTRAINT `SZ-MAN_COMP_ITEM-MEDIA_ID-FK` FOREIGN KEY (`media_id`) REFERENCES `tb_media` (`i`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `sz_update_users` (
  `update_id` bigint(20) not null,
  `user_id` int(10) unsigned not null,
  CONSTRAINT `SZ_UPD_USERS-UPD` FOREIGN KEY (`update_id`) REFERENCES `sz_update` (`id`),
  CONSTRAINT `SZ_UPD_USERS-USER` FOREIGN KEY (`user_id`) REFERENCES `tb_users` (`i`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

