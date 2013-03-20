 -- Final insert of the release version
insert into system (release_time_millis, version, release_name) values(unix_timestamp(now()), "3.8-SN", "3.8-SN");

-- CL-8742: [Server] Update CreatePendingPaymentJob to process o2 customers. Implementation of a new payment system
-- http://jira.dev.now-technologies.mobi:8181/browse/CL-8742

update tb_submittedPayments set next_sub_payment=0 where next_sub_payment is null;

alter table tb_submittedPayments modify next_sub_payment int not null;

-- CL-8742: [Server] Update CreatePendingPaymentJob to process o2 customers. Implementation of a new payment system
-- http://jira.dev.now-technologies.mobi:8181/browse/CL-8742

-- Verify on prod  alter table tb_users add column contract char(255);
alter table tb_users add column segment char(255);
alter table tb_users add column last_payment_try_in_cycle_millis BIGINT default 0;
alter table tb_users add column deactivated_grace_credit_millis BIGINT default 0;

alter table tb_paymentDetails add column phone_number varchar(255);
alter table tb_paymentPolicy add column contract char(255);
alter table tb_paymentPolicy add column segment char(255);
alter table tb_paymentPolicy add column content_category varchar(255);
alter table tb_paymentPolicy add column content_type varchar(255);
alter table tb_paymentPolicy add column content_description varchar(255);
alter table tb_paymentPolicy add column sub_merchant_id varchar(255);

-- Before 48 sms
alter table tb_users add column last_before48_sms_millis BIGINT default 0;

 create table user_logs (
   id int auto_increment,
   user_id int,
   last_update bigint,
   status char(255),
   description varchar(255),
   primary key (id)
 );
