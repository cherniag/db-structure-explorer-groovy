 -- Final insert of the release version
insert into system (release_time_millis, version, release_name) values(unix_timestamp(now()), "4.0-SN", "4.0-SN");

 -- IMP-1776 [Server] Update the Get Chart command to include Video
alter table tb_files add column duration int unsigned not null;

insert into tb_charts (name, numTracks, genre, timestamp, numBonusTracks, type)
select
'Video Chart',
ch.numTracks,
ch.genre,
ch.timestamp,
ch.numBonusTracks,
'VIDEO_CHART'
from tb_charts ch
join community_charts cc on cc.chart_id = ch.i
join tb_communities c on cc.community_id = c.i and c.rewriteURLParameter = 'o2'
where ch.type='BASIC_CHART';

insert into community_charts (chart_id, community_id)
select
ch.i,
c.i
from tb_charts ch
join tb_communities c on c.rewriteURLParameter = 'o2'
where ch.type = 'VIDEO_CHART';

insert into tb_chartDetail (subtitle, chart, position, publishTimeMillis, version, title)
select
'Video Chart',
ch.i,
8,
unix_timestamp('2013-01-01')*1000,
0,
ch.name
from tb_charts ch
join community_charts cc on cc.chart_id = ch.i
join tb_communities c on cc.community_id = c.i and c.rewriteURLParameter = 'o2'
where ch.type='VIDEO_CHART';

 -- http://jira.musicqubed.com/browse/IMP-1784
 -- [Server] Adjust payment system and jobs to support new 4G payment options
alter table tb_paymentPolicy add column tariff char(255) not null default '_3G';

-- IMP-1774 [Server] Update the Account Check command to include the Video access flags
alter table tb_users add column tariff char(255);
alter table tb_users add column videoFreeTrialHasBeenActivated boolean;

 -- http://jira.musicqubed.com/browse/IMP-1782
 -- [Server] Calculate and store the Refund when user activates Video
create table refund (
  id bigint not null auto_increment,
  log_time_millis bigint,
  next_sub_payment_millis bigint,
  payment_details_id bigint(20) not null,
  user_id int(10) not null,
  primary key (id))
 engine=INNODB DEFAULT CHARSET=utf8;

 alter table refund add index refund_PK_payment_details_id (payment_details_id), add constraint refund_U_payment_details_id foreign key (payment_details_id) references tb_paymentDetails (i);
 alter table refund add index refund_PK_user_id (user_id), add constraint refund_U_user_id foreign key (user_id) references tb_users (i);

 alter table tb_users add column last_successful_payment_details_id bigint(20);
 alter table tb_users add index tb_users_PK_last_successful_payment_details (last_successful_payment_details_id), add constraint tb_users_U_last_successful_payment_details foreign key (last_successful_payment_details_id) references tb_paymentDetails (i);

 -- IMP-1785: [Server] Add new promotion types for 4G users
 insert into tb_promotions(description, startDate, endDate, isActive, freeWeeks, userGroup, type, label, numUsers, maxUsers, subWeeks, showPromotion)
   value('o2 Free Trial for direct users', unix_timestamp(), 1606780800, true, 52, 10, 'PromoCode', 'o2_direct', 0, 0, 0, false);
 insert into tb_promotions(i, description, startDate, endDate, isActive, freeWeeks, userGroup, type, label,  numUsers, maxUsers, subWeeks, showPromotion)
   value('o2 Free Trial for indirect users', unix_timestamp(), 1606780800, true, 8, 10, 'PromoCode', 'o2_indirect', 0, 0, 0, false);
 insert into tb_promoCode(code, promotionId) select label, i from tb_promotions where label = 'o2_direct';
 insert into tb_promoCode(code, promotionId) select label, i from tb_promotions where label = 'o2_indirect';

 --
 alter table tb_users add column contractChannel varchar(255) default 'DIRECT';

 -- http://jira.musicqubed.com/browse/IMP-1794
 -- Remove video access from downgrading users
 insert into tb_accountLogTypes (i, name) value (11, "Trial skipping");
 insert into tb_accountLogTypes (i, name) value (12, "Bought period skipping");

 update tb_paymentpolicy set content_category="other";

 -- IMP-1781 [Track Repo] Migrate tracks ingestion from CMS to Track Repo
 insert into tb_fileTypes (i, name) value (4, 'VIDEO');
 alter table tb_media modify column headerFile int unsigned null;
 alter table tb_media modify column audioPreviewFile int unsigned null;
 alter table tb_media modify column headerPreviewFile int unsigned null;