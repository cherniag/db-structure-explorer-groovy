-- http://jira.musicqubed.com/browse/srv-399
-- [server] add support for freemium rule calculation logic.
use cn_service;

--  drop tables
drop table if exists user_referrals_snapshot cascade;
drop table if exists content_user_status_behavior cascade;
drop table if exists chart_user_status_behavior cascade;
drop table if exists chart_behavior cascade;
drop table if exists community_config cascade;
drop table if exists behavior_config cascade;

--  create tables and constraints
create table user_referrals_snapshot
(
  user_id int unsigned not null comment 'table pk, foreign to user_referrals.user_id',
  current_referrals int not null comment 'copy of user_referrals.current_referrals value at time the user opted-in into program.',
  required_referrals int not null comment 'copy of user_referrals.referrals_required value at time the user opted-in into program.',
  referrals_duration int comment 'copy of user_referrals.referrals_duration value at time the user opted-in into program.',
  referrals_duration_type varchar(10) comment 'copy of user_referrals.referrals_duration_type value at time the user opted-in into program.',
  matched_timestamp datetime comment 'copy of user_referrals.create_timestamp value at time the user opted-in into program.'
);
alter table user_referrals_snapshot add constraint pk_user_referrals_snapshot primary key (user_id);
alter table user_referrals_snapshot add constraint fk_user_referrals_snapshot_users foreign key (user_id) references tb_users(i);


create table behavior_config
(
  id bigint not null,
  community_id integer not null,
  type varchar(20) not null,
  required_referrals int default -1,
  referrals_duration int default -1,
  referrals_duration_type varchar(10)
);
alter table behavior_config add constraint pk_behavior_config primary key (id);
alter table behavior_config add constraint fk_behavior_config_communities foreign key (community_id) references tb_communities(id);

create table community_config
(
  community_id integer not null,
  behavior_config_id bigint not null
);
alter table community_config add constraint pk_community_config primary key (community_id);
alter table community_config add constraint fk_community_config_communities foreign key (community_id) references tb_communities(id);
alter table community_config add constraint fk_community_config_behavior_config foreign key (behavior_config_id) references behavior_config(id);

create table content_user_status_behavior
(
  id bigint not null auto_increment primary key,
  behavior_config_id bigint not null,
  user_status_type varchar(20) not null,
  are_favorites_off bit not null default 1,
  are_ads_off bit not null default 1
);
alter table content_user_status_behavior add constraint fk_content_user_status_behavior_behavior_config foreign key (behavior_config_id) references behavior_config(id);
alter table content_user_status_behavior add constraint uq_content_user_status_behavior unique (behavior_config_id, user_status_type);

create table chart_behavior
(
  id bigint not null,
  behavior_config_id bigint not null,
  type varchar(20) not null,
  is_offline bit not null default 1,
  max_tracks int default -1,
  max_tracks_duration int default -1,
  max_tracks_duration_type varchar(10) default 'HOURS',
  skip_tracks int default -1,
  skip_tracks_duration int default -1,
  skip_tracks_duration_type varchar(10) default 'HOURS',
  play_tracks_seconds int default -1
);
alter table chart_behavior add constraint pk_chart_behavior primary key (id);
alter table chart_behavior add constraint fk_chart_behavior_behavior_config foreign key (behavior_config_id) references behavior_config(id);
alter table chart_behavior add constraint uq_chart_behavior_type_behavior_config_id unique (type, behavior_config_id);

create table chart_user_status_behavior
(
  id bigint(20) not null auto_increment primary key,
  chart_id int not null,
  chart_behavior_id bigint(20) not null,
  user_status_type varchar(20) not null,
  is_locked bit not null default 0,
  action nvarchar(50)
);
alter table chart_user_status_behavior add constraint fk_chart_user_status_behavior_chart_behavior foreign key (chart_behavior_id) references chart_behavior(id);
alter table chart_user_status_behavior add constraint fk_chart_user_status_behavior_charts foreign key (chart_id) references tb_charts(i);
alter table chart_user_status_behavior add constraint uq_chart_user_status_behavior unique (chart_id, chart_behavior_id, user_status_type);

set autocommit = 0;
start transaction;

--  create default model for mtv community
set @communityid = (select id from tb_communities where name='mtv1');
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

--  create default model for mtv community
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

--  http://jira.musicqubed.com/browse/SRV-514
-- [SERVER] 6.7 CONTEXT returns required referrals -1 after 5.13.0 deploy
update behavior_config set required_referrals=5 where community_id in (select id from tb_communities where name='mtv1') and type='DEFAULT';
