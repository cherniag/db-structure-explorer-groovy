update cn_service.google_plus_user_info
set picture_url = REPLACE(picture_url, 'sz=50', 'sz=200');

alter table sz_update add column `community_id` int(11);
alter table sz_update add CONSTRAINT `sz_update_fk_community` FOREIGN KEY (`community_id`) REFERENCES `tb_communities` (`id`);
update sz_update set community_id = (
  select id from tb_communities where rewriteURLParameter = 'hl_uk'
);
