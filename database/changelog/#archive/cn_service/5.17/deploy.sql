USE cn_service;

-- SRV-547
set autocommit = 0;
start transaction;

--  create default model for mtv community
set @communityid = (select id from tb_communities where name='mtvnz');
set @modelid = 1 + (select coalesce(max(id), 0) from behavior_config);

insert into behavior_config (id, community_id, type, required_referrals) values(@modelid, @communityid, 'DEFAULT', 5);
insert into community_config (community_id, behavior_config_id) values  (@communityid, @modelid);

insert into content_user_status_behavior (behavior_config_id, user_status_type) values (@modelid, 'FREE_TRIAL');
insert into content_user_status_behavior (behavior_config_id, user_status_type) values (@modelid, 'LIMITED');
insert into content_user_status_behavior (behavior_config_id, user_status_type, are_favorites_off) values (@modelid, 'SUBSCRIBED', 0);

set @chartbehaviourtemplateid = 1 + (select coalesce(max(id), 0) from chart_behavior);
insert into chart_behavior (id, behavior_config_id, type) values (@chartbehaviourtemplateid, @modelid, 'NORMAL');
insert into chart_user_status_behavior (chart_id, chart_behavior_id, user_status_type)
  (select chart_id, @chartbehaviourtemplateid, 'SUBSCRIBED' from community_charts where community_id=@communityid)
  union (select chart_id, @chartbehaviourtemplateid, 'FREE_TRIAL' from community_charts where community_id=@communityid);

set @chartbehaviourtemplateid = 1 +  @chartbehaviourtemplateid;
insert into chart_behavior (id, behavior_config_id, type, play_tracks_seconds) values (@chartbehaviourtemplateid, @modelid, 'PREVIEW', 30);
insert into chart_user_status_behavior (chart_id, chart_behavior_id, user_status_type)
  (select chart_id, @chartbehaviourtemplateid, 'LIMITED' from community_charts where community_id=@communityid);
set @chartbehaviourtemplateid = 1 +  @chartbehaviourtemplateid;
insert into chart_behavior (id, behavior_config_id, type) values (@chartbehaviourtemplateid, @modelid, 'SHUFFLED');

commit;
set autocommit = 1;

-- SRV-562
ALTER TABLE facebook_user_info ADD COLUMN age_range_min INT;
ALTER TABLE facebook_user_info ADD COLUMN age_range_max INT;

-- SRV-391
ALTER TABLE facebook_user_info ADD COLUMN profile_image_url CHAR(255) default null;
ALTER TABLE facebook_user_info ADD COLUMN profile_image_silhouette bit(1) DEFAULT 0;

-- SRV-599
set autocommit = 0;
start transaction;

-- create default model for hl_uk community
set @communityid = (select id from tb_communities where name='hl_uk');
set @modelid = 1 + (select coalesce(max(id), 0) from behavior_config);

insert into behavior_config (id, community_id, type) values(@modelid, @communityid, 'DEFAULT');
insert into community_config (community_id, behavior_config_id) values  (@communityid, @modelid);

insert into content_user_status_behavior (behavior_config_id, user_status_type) values (@modelid, 'FREE_TRIAL');
insert into content_user_status_behavior (behavior_config_id, user_status_type) values (@modelid, 'LIMITED');
insert into content_user_status_behavior (behavior_config_id, user_status_type, are_favorites_off) values (@modelid, 'SUBSCRIBED', 0);

set @chartbehaviourtemplateid = 1 + (select coalesce(max(id), 0) from chart_behavior);
insert into chart_behavior (id, behavior_config_id, type) values (@chartbehaviourtemplateid, @modelid, 'NORMAL');
insert into chart_user_status_behavior (chart_id, chart_behavior_id, user_status_type)
  (select chart_id, @chartbehaviourtemplateid, 'SUBSCRIBED' from community_charts where community_id=@communityid)
  union (select chart_id, @chartbehaviourtemplateid, 'FREE_TRIAL' from community_charts where community_id=@communityid);

set @chartbehaviourtemplateid = 1 +  @chartbehaviourtemplateid;
insert into chart_behavior (id, behavior_config_id, type, play_tracks_seconds) values (@chartbehaviourtemplateid, @modelid, 'PREVIEW', 30);
insert into chart_user_status_behavior (chart_id, chart_behavior_id, user_status_type)
  (select chart_id, @chartbehaviourtemplateid, 'LIMITED' from community_charts where community_id=@communityid);
set @chartbehaviourtemplateid = 1 +  @chartbehaviourtemplateid;
insert into chart_behavior (id, behavior_config_id, type) values (@chartbehaviourtemplateid, @modelid, 'SHUFFLED');

--  create default model for hl_uk community
set @modelid = 1 + (select coalesce(max(id), 0) from behavior_config);

insert into behavior_config (id, community_id, type) values(@modelid, @communityid, 'FREEMIUM');

insert into content_user_status_behavior (behavior_config_id, user_status_type) values (@modelid, 'FREE_TRIAL');
insert into content_user_status_behavior (behavior_config_id, user_status_type) values (@modelid, 'LIMITED');
insert into content_user_status_behavior (behavior_config_id, user_status_type, are_favorites_off) values (@modelid, 'SUBSCRIBED', 0);

set @chartbehaviourtemplateid = 1 + (select coalesce(max(id), 0) from chart_behavior);
insert into chart_behavior (id, behavior_config_id, type) values (@chartbehaviourtemplateid, @modelid, 'NORMAL');
insert into chart_user_status_behavior (chart_id, chart_behavior_id, user_status_type)
  (select chart_id, @chartbehaviourtemplateid, 'SUBSCRIBED' from community_charts where community_id=@communityid)
  union (select chart_id, @chartbehaviourtemplateid, 'FREE_TRIAL' from community_charts where community_id=@communityid);

set @chartbehaviourtemplateid = 1 +  @chartbehaviourtemplateid;
insert into chart_behavior (id, behavior_config_id, type, play_tracks_seconds, is_offline) values (@chartbehaviourtemplateid, @modelid, 'PREVIEW', 30, 0);
insert into chart_user_status_behavior (chart_id, chart_behavior_id, user_status_type)
  (select chart_id, @chartbehaviourtemplateid, 'LIMITED' from community_charts where community_id=@communityid);

set @chartbehaviourtemplateid = 1 +  @chartbehaviourtemplateid;
insert into chart_behavior (id, behavior_config_id, type, is_offline) values (@chartbehaviourtemplateid, @modelid, 'SHUFFLED', 0);

commit;
set autocommit = 1;

-- SRV-602
set autocommit = 0;
start transaction;

-- create default model for hl_uk community
set @communityid = (select id from tb_communities where name='hl_uk');
set @modelid = 1 + (select coalesce(max(id), 0) from behavior_config);

insert into behavior_config (id, community_id, type) values(@modelid, @communityid, 'DEFAULT');
insert into community_config (community_id, behavior_config_id) values  (@communityid, @modelid);

insert into content_user_status_behavior (behavior_config_id, user_status_type) values (@modelid, 'FREE_TRIAL');
insert into content_user_status_behavior (behavior_config_id, user_status_type) values (@modelid, 'LIMITED');
insert into content_user_status_behavior (behavior_config_id, user_status_type, are_favorites_off) values (@modelid, 'SUBSCRIBED', 0);

set @chartbehaviourtemplateid = 1 + (select coalesce(max(id), 0) from chart_behavior);
insert into chart_behavior (id, behavior_config_id, type) values (@chartbehaviourtemplateid, @modelid, 'NORMAL');
insert into chart_user_status_behavior (chart_id, chart_behavior_id, user_status_type)
  (select chart_id, @chartbehaviourtemplateid, 'SUBSCRIBED' from community_charts where community_id=@communityid)
  union (select chart_id, @chartbehaviourtemplateid, 'FREE_TRIAL' from community_charts where community_id=@communityid);

set @chartbehaviourtemplateid = 1 +  @chartbehaviourtemplateid;
insert into chart_behavior (id, behavior_config_id, type, play_tracks_seconds) values (@chartbehaviourtemplateid, @modelid, 'PREVIEW', 30);
insert into chart_user_status_behavior (chart_id, chart_behavior_id, user_status_type)
  (select chart_id, @chartbehaviourtemplateid, 'LIMITED' from community_charts where community_id=@communityid);
set @chartbehaviourtemplateid = 1 +  @chartbehaviourtemplateid;
insert into chart_behavior (id, behavior_config_id, type) values (@chartbehaviourtemplateid, @modelid, 'SHUFFLED');

--  create default model for hl_uk community
set @modelid = 1 + (select coalesce(max(id), 0) from behavior_config);

insert into behavior_config (id, community_id, type) values(@modelid, @communityid, 'FREEMIUM');

insert into content_user_status_behavior (behavior_config_id, user_status_type) values (@modelid, 'FREE_TRIAL');
insert into content_user_status_behavior (behavior_config_id, user_status_type) values (@modelid, 'LIMITED');
insert into content_user_status_behavior (behavior_config_id, user_status_type, are_favorites_off) values (@modelid, 'SUBSCRIBED', 0);

set @chartbehaviourtemplateid = 1 + (select coalesce(max(id), 0) from chart_behavior);
insert into chart_behavior (id, behavior_config_id, type) values (@chartbehaviourtemplateid, @modelid, 'NORMAL');
insert into chart_user_status_behavior (chart_id, chart_behavior_id, user_status_type)
  (select chart_id, @chartbehaviourtemplateid, 'SUBSCRIBED' from community_charts where community_id=@communityid)
  union (select chart_id, @chartbehaviourtemplateid, 'FREE_TRIAL' from community_charts where community_id=@communityid);

set @chartbehaviourtemplateid = 1 +  @chartbehaviourtemplateid;
insert into chart_behavior (id, behavior_config_id, type, play_tracks_seconds, is_offline) values (@chartbehaviourtemplateid, @modelid, 'PREVIEW', 30, 0);
insert into chart_user_status_behavior (chart_id, chart_behavior_id, user_status_type)
  (select chart_id, @chartbehaviourtemplateid, 'LIMITED' from community_charts where community_id=@communityid);

set @chartbehaviourtemplateid = 1 +  @chartbehaviourtemplateid;
insert into chart_behavior (id, behavior_config_id, type, is_offline) values (@chartbehaviourtemplateid, @modelid, 'SHUFFLED', 0);

commit;
set autocommit = 1;

-- SRV-619
CREATE TABLE opted_out_users (
  id int AUTO_INCREMENT PRIMARY KEY,
  email varchar(100) CHARACTER SET utf8 NOT NULL,
  create_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP
);

ALTER TABLE opted_out_users ADD UNIQUE INDEX uq_opted_out_users_email (email);


INSERT INTO opted_out_users (email)
VALUES ('janeaway58@hotmail.com'),
  ('lauradempster91@gmail.com'),
  ('alityco1@gmail.com'),
  ('hannah.m.barrow@gmail.com'),
  ('adamson.liz59@gmail.com');

-- SRV-628
DELETE FROM urban_airship_token WHERE token = '(null)';

-- SRV-632
ALTER TABLE tb_userAndroidDetails DROP FOREIGN KEY INDEX_ON_ANDROID_USERUID_TB_USER_I;


ALTER TABLE tb_useriPhoneDetails DROP FOREIGN KEY INDEX_ON_IPHONE_USERUID_TB_USER_I;

-- SRV-631
insert into system (release_time_millis, version, release_name) values(unix_timestamp(), "5.17", "5.17");