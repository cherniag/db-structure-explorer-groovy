update cn_service.google_plus_user_info
set picture_url = REPLACE(picture_url, 'sz=50', 'sz=200');

alter table sz_update add column `community_id` int(11);
alter table sz_update add CONSTRAINT `sz_update_fk_community` FOREIGN KEY (`community_id`) REFERENCES `tb_communities` (`id`);
update sz_update set community_id = (
  select id from tb_communities where rewriteURLParameter = 'hl_uk'
);

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
