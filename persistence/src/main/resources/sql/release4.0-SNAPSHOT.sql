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
0,
unix_timestamp('2013-01-01')*1000,
0,
ch.name
from tb_charts ch
join community_charts cc on cc.chart_id = ch.i
join tb_communities c on cc.community_id = c.i and c.rewriteURLParameter = 'o2'
where ch.type='VIDEO_CHART';

 -- http://jira.musicqubed.com/browse/IMP-1784
 -- [Server] Adjust payment system and jobs to support new 4G payment options
alter table tb_paymentpolicy add column tariff char(255) not null default '_3G';

 alter table tb_users add column tariff char(255);

-- IMP-1774 [Server] Update the Account Check command to include the Video access flags
alter table tb_users add column tariff char(255);
alter table tb_users add column videoFreeTrialHasBeenActivated boolean;
alter table tb_users add column hasAllDetails boolean;
alter table tb_users add column showFreeTrial boolean;

 -- http://jira.musicqubed.com/browse/IMP-1782
 -- [Server] Calculate and store the Refund when user activates Video
create table data_to_do_refund (
  id bigint not null auto_increment,
  log_time_millis bigint,
  next_sub_payment_millis bigint,
  payment_details_id bigint(20) not null,
  user_id int(11) not null,
  primary key (id))
 engine=INNODB DEFAULT CHARSET=utf8;

 alter table data_to_do_refund add index data_to_do_refund_PK_payment_details_id (payment_details_id), add constraint data_to_do_refund_U_payment_details_id foreign key (payment_details_id) references tb_paymentDetails (i);
 alter table data_to_do_refund add index data_to_do_refund_PK_user_id (user_id), add constraint data_to_do_refund_U_user_id foreign key (user_id) references tb_users (i);

 -- IMP-1785: [Server] Add new promotion types for 4G users
 insert into tb_promotions(description, startDate, endDate, isActive, freeWeeks, userGroup, type, label)
   value('o2 Free Trial for direct users', 1356342064, 1606780800, true, 48, 10, 'PromoCode', 'o2_direct');
 insert into tb_promotions(i, description, startDate, endDate, isActive, freeWeeks, userGroup, type, label)
   value('o2 Free Trial for indirect users', 1356342064, 1606780800, true, 8, 10, 'PromoCode', 'o2_indirect');
 insert into tb_promoCode(code, promotionId) select label, i from tb_promoCode where label = 'o2_direct';
 insert into tb_promoCode(code, promotionId) select label, i from tb_promoCode where label = 'o2_indirect';
