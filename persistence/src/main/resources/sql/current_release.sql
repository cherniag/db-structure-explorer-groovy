-- google plus fix
update cn_service.google_plus_user_info
set picture_url = REPLACE(picture_url, 'sz=50', 'sz=200');

-- streamzine community
alter table sz_update add column `community_id` int(11);
alter table sz_update add CONSTRAINT `sz_update_fk_community` FOREIGN KEY (`community_id`) REFERENCES `tb_communities` (`id`);
update sz_update set community_id = (
  select id from tb_communities where rewriteURLParameter = 'hl_uk'
);

-- streamzine badges
update cn_service.google_plus_user_info
set picture_url = REPLACE(picture_url, 'sz=50', 'sz=200');

use cn_service;
delete from sz_filename_alias;
alter TABLE sz_filename_alias add column width int NOT NULL;
alter TABLE sz_filename_alias add column height int NOT NULL;
alter table sz_filename_alias drop INDEX `filename_alias_UK_alias_domain`;
alter table sz_filename_alias drop INDEX `file_name`;
ALTER TABLE sz_filename_alias ADD CONSTRAINT filename_alias_UK_alias_domain UNIQUE (file_name, domain);

CREATE TABLE `sz_resolution` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `device_type` varchar(128) DEFAULT NULL,
  `width` int(11) NOT NULL,
  `height` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `sz_resolution_dev_w_h` (`device_type`,`width`,`height`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `sz_badge_mapping` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `original_alias_id` bigint(20) NOT NULL,
  `filename_alias_id` bigint(20),
  `resolution_id` bigint(20),
  `community_id` int(11) NOT NULL,
  `uploaded` datetime NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `sz_badge_mapping_uk_c_res_fs` (`community_id`,`resolution_id`,`filename_alias_id`,`original_alias_id`),
  CONSTRAINT `bm_curr_alias` FOREIGN KEY (`filename_alias_id`) REFERENCES `sz_filename_alias` (`id`),
  CONSTRAINT `bm_orig_alias` FOREIGN KEY (`original_alias_id`) REFERENCES `sz_filename_alias` (`id`),
  CONSTRAINT `bm_resolution` FOREIGN KEY (`resolution_id`) REFERENCES `sz_resolution` (`id`),
  CONSTRAINT `bm_community` FOREIGN KEY (`community_id`) REFERENCES `tb_communities` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

update sz_block set badge_url=null;
alter table sz_block change badge_url badge_filename_id bigint(20);
alter table sz_block add CONSTRAINT `badge_file_id_fk` FOREIGN KEY (`badge_filename_id`) REFERENCES `sz_filename_alias` (`id`);

-- http://jira.musicqubed.com/browse/SRV-171
-- Update deeplinks creation logic for playlist deeplinks in magazine channel

start transaction;

delete from sz_deeplink_promotional;
delete from sz_granted_to_types;
delete from sz_block_access_policy;
delete from sz_deeplink_music_list;
delete from sz_block;
delete from sz_deeplink_man_compilation;
delete from sz_deeplink_music_track;
delete from sz_deeplink_news_list;
delete from sz_deeplink_news_story;
delete from sz_filename_alias;
delete from sz_man_compilation_items;
delete from sz_update;
delete from sz_update_users;

commit;

alter table sz_deeplink_music_list drop column chart_type;
alter table sz_deeplink_music_list add column chart_detail_id int(10) unsigned DEFAULT NULL;
