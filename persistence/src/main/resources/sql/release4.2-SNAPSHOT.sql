 -- Final insert of the release version
insert into system (release_time_millis, version, release_name) values(unix_timestamp(now()), "4.1-SN", "4.1-SN");

-- Allow whitestisted MSISDNs have access to video and locked tracks
-- http://jira.musicqubed.com/browse/IMP-2311
alter table tb_promotions add column is_white_listed BIT default false;

-- http://jira.musicqubed.com/browse/IMP-1783
-- IMP-1783 - [Server] Auto-subscribe new O2 4G and non-4G users activating free trial
alter table tb_paymentPolicy add column is_default bit default false;

ALTER TABLE tb_paymentDetails DROP FOREIGN KEY FK60BC1B4D10EFFD28;
ALTER TABLE tb_promotionpaymentpolicy_tb_paymentpolicy DROP FOREIGN KEY FK834CEA00C875CF15;

alter table tb_paymentPolicy modify column i int not null auto_increment;
alter table tb_paymentDetails modify column paymentPolicyId int;
alter table tb_promotionpaymentpolicy_tb_paymentpolicy modify column paymentPolicies_i int default null;

alter table tb_paymentDetails add index tb_paymentDetails_PK_paymentPolicyId (paymentPolicyId), add constraint tb_paymentDetails_U_paymentPolicyId foreign key (paymentPolicyId) references tb_paymentPolicy (i);
alter table tb_promotionpaymentpolicy_tb_paymentpolicy add index tb_promotionpaymentpolicy_tb_paymentpolicy_PK_paymentPolicies_i (paymentPolicies_i), add constraint tb_promotionpaymentpolicy_tb_paymentpolicy_U_paymentPolicies_i foreign key (paymentPolicies_i) references tb_paymentPolicy (i);

start transaction;

update tb_paymentPolicy set provider='O2' where provider='o2';
update tb_paymentPolicy set provider='NON_O2' where provider='non-o2';
update tb_users set provider='O2' where provider='o2';
update tb_users set provider='NON_O2' where provider='non-o2';

commit;

