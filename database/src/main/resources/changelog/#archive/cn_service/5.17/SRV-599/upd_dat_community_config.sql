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