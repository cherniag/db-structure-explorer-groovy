 -- Final insert of the release version
insert into system (release_time_millis, version, release_name) values(unix_timestamp(now()), "3.9-SN", "3.9-SN");

 -- IMP-1261 [MOBILE WEB] Error Messaging
alter table tb_paymentDetails add column errorCode varchar(255);