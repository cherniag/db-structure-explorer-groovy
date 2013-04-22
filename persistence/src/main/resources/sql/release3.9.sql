 -- Final insert of the release version
insert into system (release_time_millis, version, release_name) values(unix_timestamp(now()), "3.9-SN", "3.9-SN");

-- IMP-1263 [BILLING] Track users changing segment and unsubscribe them
alter table tb_paymentPolicy add column provider char(255);
