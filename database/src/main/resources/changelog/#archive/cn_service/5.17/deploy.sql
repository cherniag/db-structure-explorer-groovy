USE cn_service;

--SRV-547
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

--SRV-562
ALTER TABLE facebook_user_info
  ADD COLUMN age_range_min INT,
  ADD COLUMN age_range_max INT;

--SRV-391
ALTER TABLE facebook_user_info
  ADD COLUMN profile_image_url CHAR(255) default null,
  ADD COLUMN profile_image_silhouette bit(1) DEFAULT 0;

--SRV-599
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

--SRV-602
SET autocommit = 0;
start transaction;

select @first_new_id:=coalesce(max(id), 0) + 1 from client_version_messages;
set @second_new_id = @first_new_id + 1;
set @third_new_id = @first_new_id + 2;
set @forth_new_id = @first_new_id + 3;

insert into client_version_messages
(id            ,             message_key                                   , url) values
(@first_new_id , 'service.config.mtv.tracks.FORCED_UPDATE.WINDOWS.1.0.6-p' , 'http://www.windowsphone.com/s?appid=73e8aedd-babf-4e94-87d6-899464f1c49c'),
(@second_new_id, 'service.config.mtv.tracks.FORCED_UPDATE.WINDOWS.1.0.7-p' , 'http://www.windowsphone.com/s?appid=73e8aedd-babf-4e94-87d6-899464f1c49c'),
(@third_new_id , 'service.config.mtv.tracks.FORCED_UPDATE.WINDOWS.1.0.10-p', 'http://www.windowsphone.com/s?appid=73e8aedd-babf-4e94-87d6-899464f1c49c'),
(@forth_new_id , 'service.config.mtv.tracks.FORCED_UPDATE.ANDROID.2.0.0'   , 'https://play.google.com/store/apps/details?id=com.musicqubed.mtv');

select @community_id:=id from tb_communities where name='mtv1';
select @windows:=i from tb_deviceTypes where name='WINDOWS_PHONE';
select @android:=i from tb_deviceTypes where name='ANDROID';

insert into client_version_info
(device_type_id, community_id ,  major_number, minor_number, revision_number, qualifier, message_id    , status         , application_name) values
( @windows     , @community_id, 1            , 0           , 6              , 'p'      , @first_new_id , 'FORCED_UPDATE', 'mtv-tracks'),
( @windows     , @community_id, 1            , 0           , 7              , 'p'      , @second_new_id, 'FORCED_UPDATE', 'mtv-tracks'),
( @windows     , @community_id, 1            , 0           , 10             , 'p'      , @third_new_id , 'FORCED_UPDATE', 'mtv-tracks'),
( @android     , @community_id, 2            , 0           , 0              , null     , @forth_new_id , 'FORCED_UPDATE', 'mtv-tracks');

commit;
SET autocommit = 1;

--SRV-619
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

--SRV-628
DELETE FROM urban_airship_token WHERE token = '(null)';

--SRV-632
ALTER TABLE tb_userAndroidDetails DROP FOREIGN KEY INDEX_ON_ANDROID_USERUID_TB_USER_I;
ALTER TABLE tb_useriPhoneDetails DROP FOREIGN KEY INDEX_ON_IPHONE_USERUID_TB_USER_I;

--SRV-631
insert into system (release_time_millis, version, release_name) values(unix_timestamp(), "5.17", "5.17");
