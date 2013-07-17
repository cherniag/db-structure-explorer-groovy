 -- Final insert of the release version
insert into system (release_time_millis, version, release_name) values(unix_timestamp(now()), "4.0-SN", "4.0-SN");

 -- IMP-1776 [Server] Update the Get Chart command to include Video
alter table tb_files add column duration int unsigned not null;
alter table tb_charts modify column i int unsigned not null;
alter table tb_chartDetail modify column chart int unsigned not null;

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

 insert into tb_promoCode(id, code, promotionId) value(31, 'o2_direct', 33);
 insert into tb_promoCode(id, code, promotionId) value(32, 'o2_indirect', 34);
 insert into tb_promotions(i, description, startDate, endDate, isActive, freeWeeks, userGroup, type, label)
   value(33, 'o2 Free Trial for direct users', 1356342064, 1606780800, true, 48, 10, 'PromoCode', 'o2_direct');
 insert into tb_promotions(i, description, startDate, endDate, isActive, freeWeeks, userGroup, type, label)
   value(34, 'o2 Free Trial for indirect users', 1356342064, 1606780800, true, 8, 10, 'PromoCode', 'o2_indirect');
