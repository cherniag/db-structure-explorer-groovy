insert into system (release_time_millis, version, release_name) values(unix_timestamp(), "5.14", "5.14");

-- SRV-425
use cn_service;

alter table tb_paymentPolicy add column payment_policy_type varchar(255) not null default 'RECURRENT' comment 'enum values are RECURRENT and ONETIME' after paymentType;
alter table tb_submittedPayments add column payment_policy_id int(11) comment 'reference to payment policy being used' after paymentDetailsId;
-- end of SRV-425