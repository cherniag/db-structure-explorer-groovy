-- Final insert of the release version
insert into system (release_time_millis, version, release_name) values(unix_timestamp(now()), "3.5.0-SN", "Impetuous Zebra");

-- JAdmin Manage chart
alter table tb_chartDetail change chart chart TINYINT not null;
alter table tb_chartDetail add column publishTimeMillis bigint not null;

-- Adding version number to chartDetails in order on client to get info about changes of the chart item content
alter table tb_chartDetail add column version int not null;

-- Adding version number to media file in order on client to get info about changes of the media file content
alter table tb_files add column version int not null;

delete from tb_chartDetail where i=322;
delete from tb_chartDetail where i=323;

-- Removing old commnities
delete from tb_chartDetail where chart not in (3,6,7);

alter table tb_charts modify column i TINYINT not null auto_increment;
delete from tb_chartDetail where tb_chartDetail.chart=2;

alter table tb_chartDetail add index tb_chartdetail_PK_chart (chart), add constraint tb_chartdetail_U_chart foreign key (chart) references tb_charts (i);
alter table tb_chartDetail add index tb_chartdetail_PK_media (media), add constraint tb_chartdetail_U_media foreign key (media) references tb_media (i);

create unique index tb_chartDetail_U_3_1 on tb_chartDetail(media,chart,publishTimeMillis);

update tb_chartDetail set channel=null where channel="";

-- CL-6963: Add functionality to store user type for user
-- http://jira.dev.now-technologies.mobi:8181/browse/CL-6963
alter table tb_userTypes modify column i INT UNSIGNED not null auto_increment;
alter table tb_users modify column userType INT UNSIGNED not null default 0;

insert into tb_userTypes (i, name) value (0, 'UNDEFINED');

-- CL-6959: Tune free SMS sending
-- http://jira.dev.now-technologies.mobi:8181/browse/CL-6959
alter table tb_users add column amountOfMoneyToUserNotification decimal(5,2) not null default 0, add column lastSuccesfullPaymentSmsSendingTimestampMillis bigint not null default 0;
-- modified genre id type to add new genres
alter table tb_genres modify column i INT UNSIGNED not null default 0;
alter table tb_media modify column genre INT UNSIGNED not null default 0;
alter table tb_charts modify column genre INT UNSIGNED not null default 0;
-- right init hibernate_sequences
UPDATE hibernate_sequences seq INNER JOIN (select max(m.i)+1 max_i from tb_media m) max_i on seq.sequence_name = 'Item' SET seq.sequence_next_hi_value = max_i.max_i;

-- Changes to isOnFreeTrial method
-- use old php server (support MetalHummer)| alter table tb_users drop column freeBalance;
alter table tb_users change freeBalance freeBalance tinyint(3) unsigned NULL;
alter table tb_users add column freeTrialExpiredMillis bigint DEFAULT NULL;

-- CL-7167: JAdmin > Add transactions history per user
-- http://jira.dev.now-technologies.mobi:8181/browse/CL-7167
alter table tb_accountLog modify column relatedPaymentUID BIGINT null;
alter table tb_accountLog modify column relatedMediaUID INT UNSIGNED null;
alter table tb_accountLog modify column transactionType INT not null;

update tb_accountLog set relatedPaymentUID=null where relatedPaymentUID=0;
update tb_accountLog set relatedMediaUID=null where relatedMediaUID=0;

-- alter table tb_accountLog add index tb_accountLog_PK_relatedMediaUID (relatedMediaUID), add constraint tb_accountLog_U_relatedMediaUID foreign key (relatedMediaUID) references tb_media (i);
-- alter table tb_accountLog add index tb_accountLog_PK_relatedPaymentUID (relatedPaymentUID), add constraint tb_accountLog_U_relatedPaymentUID foreign key (relatedPaymentUID) references tb_submittedPayments (i);

-- CL-7394: Implement new promotion for trial users subscribing within a specified period
-- http://jira.dev.now-technologies.mobi:8181/browse/CL-7394
alter table tb_users add column freeTrialStartedTimestampMillis bigint DEFAULT NULL;

-- alter table tb_promotions add column activeSinceTrialStartTimestampMillis bigint DEFAULT NULL;
-- alter table tb_promotions add column activeTillTrialEndTimestampMillis bigint DEFAULT NULL;

alter table tb_filter_params add column activeSinceTrialStartTimestampMillis bigint DEFAULT NULL;
alter table tb_filter_params add column activeTillTrialEndTimestampMillis bigint DEFAULT NULL;
alter table tb_filter_params add column FreeTrialPeriodFilter_id TINYINT UNSIGNED not null;

-- alter table tb_filter_params add index tb_filter_params_PK_FreeTrialPeriodFilter_id (FreeTrialPeriodFilter_id), add constraint tb_filter_params_U_FreeTrialPeriodFilter_id foreign key (FreeTrialPeriodFilter_id) references tb_filter(id);

alter table tb_promotionPaymentPolicy_tb_paymentPolicy drop index paymentPolicies_i;