 -- Final insert of the release version
insert into system (release_time_millis, version, release_name) values(unix_timestamp(now()), "3.8-SN", "3.8-SN");

-- CL-8742: [Server] Update CreatePendingPaymentJob to process o2 customers. Implementation of a new payment system
-- http://jira.dev.now-technologies.mobi:8181/browse/CL-8742

update tb_submittedpayments set next_sub_payment=0 where next_sub_payment is null;

alter table tb_submittedpayments modify next_sub_payment int not null;

-- CL-8742: [Server] Update CreatePendingPaymentJob to process o2 customers. Implementation of a new payment system
-- http://jira.dev.now-technologies.mobi:8181/browse/CL-8742

alter table tb_users add column contract char(255);
alter table tb_users add column segment char(255);
alter table tb_users add column last_payment_try_millis BIGINT default 0;
alter table tb_users add column full_grace_credit_millis BIGINT default 0;

alter table tb_paymentdetails add column phoneNumber varchar(255);