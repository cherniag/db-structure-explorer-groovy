 -- Final insert of the release version
insert into system (release_time_millis, version, release_name) values(unix_timestamp(now()), "4.2-SN", "4.2-SN");

-- http://jira.musicqubed.com/browse/IMP-1783
-- IMP-1783 - [Server] Auto-subscribe new O2 4G and non-4G users activating free trial
alter table tb_paymentPolicy add column is_default bit default false;

ALTER TABLE cn_service.tb_paymentDetails DROP INDEX FK60BC1B4D10EFFD28;
ALTER TABLE cn_service.tb_promotionPaymentPolicy_tb_paymentPolicy DROP INDEX FK834CEA00C875CF15;

alter table tb_paymentPolicy modify column i int not null auto_increment;
alter table tb_paymentDetails modify column paymentPolicyId int;
alter table tb_promotionPaymentPolicy_tb_paymentPolicy modify column paymentPolicies_i int default null;

alter table tb_paymentDetails add index tb_paymentDetails_PK_paymentPolicyId (paymentPolicyId);
alter table tb_promotionPaymentPolicy_tb_paymentPolicy add index tb_promotionPaymentPolicy_tb_paymentPolicy_PK_paymentPolicies_i (paymentPolicies_i);

start transaction;

update tb_paymentPolicy set provider='O2' where provider='o2';
update tb_paymentPolicy set provider='NON_O2' where provider='non-o2';
update tb_users set provider='O2' where provider='o2';
update tb_users set provider='NON_O2' where provider='non-o2';

commit;

