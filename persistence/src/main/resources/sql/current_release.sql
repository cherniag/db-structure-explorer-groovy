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

ALTER TABLE sz_deeplink_promotional ADD COLUMN opener varchar(15);

update sz_deeplink_promotional
set opener = 'IN_APP' where link_type = 'EXTERNAL_AD';

-- http://jira.musicqubed.com/browse/SRV-171
-- Update deeplinks creation logic for playlist deeplinks in magazine channel

create table chart_types (id bigint(20) NOT NULL, name char(25) NOT NULL);

alter table sz_deeplink_music_list add column chart_id tinyint(4) DEFAULT NULL;
alter table sz_deeplink_music_list add index sz_deeplink_music_list_PK_chart_id (chart_id), add constraint sz_deeplink_music_list_U_chart_id foreign key (chart_id) references tb_charts (i);

start transaction;

insert into chart_types (id, name) values (0, 'BASIC_CHART'), (1, 'HOT_TRACKS'), (2, 'FOURTH_CHART'), (3, 'OTHER_CHART'), (4, 'FIFTH_CHART'), (5, 'VIDEO_CHART'), (6, 'HL_UK_PLAYLIST_1'), (7, 'HL_UK_PLAYLIST_2');

UPDATE
    sz_deeplink_music_list dml JOIN sz_block b
      ON b.id = dml.block_id JOIN sz_update u
      ON u.id = b.update_id JOIN chart_types ct
      ON ct.id = dml.chart_type JOIN tb_charts c
      ON c.type = ct.name COLLATE utf8_unicode_ci JOIN community_charts cc
      ON cc.chart_id = c.i
         AND cc.community_id = u.community_id
SET
  dml.chart_id = c.i
;

commit;

drop table chart_types;
alter table sz_deeplink_music_list drop column chart_type;

-- GO-1887 tb_SubmittedPayments isn't updated if the error message is longer than 255 chars
alter table tb_submittedPayments change column `descriptionError` `descriptionError` varchar(4000) DEFAULT NULL;
alter table tb_paymentDetails change column `descriptionError` `descriptionError` varchar(4000) DEFAULT NULL;

-- SRV-192
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
  `qualifier` char(32),
  `message_id` bigint(20) NOT NULL,
  `status` char(100) NOT NULL,
  `application_name` char(100) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


alter table client_version_info add CONSTRAINT `client_version_info_community_id_fk` FOREIGN KEY (`community_id`) REFERENCES `tb_communities` (`id`);
alter table client_version_info add CONSTRAINT `client_version_info_device_type_id_fk` FOREIGN KEY (`device_type_id`) REFERENCES `tb_deviceTypes` (`i`);
alter table client_version_info add CONSTRAINT `client_version_info_message_id_fk` FOREIGN KEY (`message_id`) REFERENCES `client_version_messages` (`id`);

alter table client_version_info add UNIQUE KEY `client_version_info_uk` (`community_id`,`device_type_id`,`application_name`,`status`);

