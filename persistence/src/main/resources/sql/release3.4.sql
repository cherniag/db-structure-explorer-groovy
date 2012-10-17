create table messages
(
   id integer not null auto_increment,
   newsDetail INT UNSIGNED not null,
   activated bit not null,
   body longtext not null,
   community_id tinyint unsigned not null,
   frequence varchar(255),
   imageFileName varchar(255),
   messageType varchar(255),
   position tinyint not null,
   publishTimeMillis bigint not null,
   title varchar(255) not null,
   primary key (id),
   unique (position, community_id, messageType, publishTimeMillis),
   constraint FK9C2397E710B7C5D2 foreign key (community_id) references tb_communities (i)
) ENGINE=InnoDB default charset=utf8;

create table filters
(
   id integer not null auto_increment,
   name varchar(255) not null,
   primary key (id),
   unique (name)
)
ENGINE=InnoDB default charset=utf8;

create table messages_filters (
	messages_id integer not null,
	filterWithCtiteria_id integer not null,
	primary key (messages_id, filterWithCtiteria_id),
	index FKE99CCED0813E3712 (messages_id),
	index FKE99CCED01B11A7A4 (filterWithCtiteria_id),
	constraint FKE99CCED0813E3712 foreign key (messages_id) references messages (id),
	constraint FKE99CCED01B11A7A4 foreign key (filterWithCtiteria_id) references filters(id)
) ENGINE=InnoDB default charset=utf8;

start transaction;

insert into messages
(
   newsDetail,activated, body, frequence, messageType, title, publishTimeMillis, position, community_id
)

(
   select
   tb_newsDetail.i,
   tb_newsDetail.online,
   tb_newsDetail.body,
   tb_newsDetail.messageFrequence,
   tb_newsDetail.messageType,
   tb_newsDetail.item,
   UNIX_TIMESTAMP()*1000,
   tb_newsDetail.position,
   tb_news.community
   from tb_newsDetail join tb_news on tb_newsDetail.news=tb_news.i
);

insert into filters (name) value ('LAST_TRIAL_DAY'), ('NOT_ACTIVE_PAYMENT_DETAILS_OR_NO_PAYMENT_DETAILS'), ('FREE_TRIAL'), ('PAYMENT_ERROR'), ('LIMITED_AFTER_TRIAL'), ('LIMITED'), ('ANDROID'), ('BLACKBERRY'), ('IOS'), ('J2ME');

insert into messages_filters
(
   messages_id, filterWithCtiteria_id
)

(
   select
   messages.id, filters.id
   from tb_filter_tb_newsDetail join tb_newsDetail on tb_filter_tb_newsDetail.newDetails_i=tb_newsDetail.i join filters on filters.name=tb_newsDetail.userState
   join tb_filter on tb_filter.id=tb_filter_tb_newsDetail.filters_id
   join messages on messages.newsDetail=tb_filter_tb_newsDetail.newDetails_i
   where tb_filter.filterType='UserStateFilter'
)
;

insert into messages_filters
(
   messages_id, filterWithCtiteria_id
)

(
   select
   messages.id, filters.id
   from tb_filter_tb_newsDetail join tb_newsDetail on tb_filter_tb_newsDetail.newDetails_i=tb_newsDetail.i join filters on filters.name=tb_newsDetail.userHandset
   join tb_filter on tb_filter.id=tb_filter_tb_newsDetail.filters_id
   join messages on messages.newsDetail=tb_filter_tb_newsDetail.newDetails_i
   where tb_filter.filterType='UserHandsetFilter'
)
;

alter table messages drop column newsDetail;

commit;


------------
-- CL-6217 [Samsung task] Change the logic for the promotional weeks
------------
create table not_promoted_devices (
  deviceUID varchar(255) not null,
  community_id tinyint not null,
  primary key  (deviceUID),
  unique key DEVICE_UID_INDEX (deviceUID)
) engine=InnoDB default charset=utf8;

create table promoted_devices (
  deviceUID varchar(255) not null,
  community_id tinyint not null,
  primary key  (deviceUID),
  unique key DEVICE_UID_INDEX (deviceUID)
) engine=InnoDB default charset=utf8;

start transaction;
-- in select "7" is the id of samsung community
insert into promoted_devices (deviceUID, community_id)
select deviceUID, 7 from tb_promotedDevice;
commit;

-- drop table tb_promotedDevice; We can't drop it bacause the portal project uses persistence project of old version

------------
-- CL-6307 Server > Change initial user journey > Split registration & promotion
------------
alter table tb_users add column potentialPromoCodePromotion_i TINYINT unsigned;
alter table tb_users add index FKFAEDF4F753C7738F (potentialPromoCodePromotion_i), add constraint FKFAEDF4F753C7738F foreign key (potentialPromoCodePromotion_i) references tb_promotions (i);

------------
-- CL-XXXX Server > Adding system table for database in order to keep version of the database
------------
create table system (
	id integer not null auto_increment,
	release_time_millis bigint not null,
	version char(8) not null,
	release_name varchar(255),
	primary key(id),
	unique index version_index (version)
) engine=InnoDB default charset=utf8;

insert into system (release_time_millis, version, release_name) values(unix_timestamp(now()), "3.4.0-SN", "");
