 -- Final insert of the release version
insert into system (release_time_millis, version, release_name) values(unix_timestamp(now()), "3.9-SN", "3.9-SN");

-- IMP-1263 [BILLING] Track users changing segment and unsubscribe them
alter table tb_paymentPolicy add column provider char(255);

update tb_paymentPolicy pp set pp.provider = 'non-o2' where pp.segment is null and pp.communityID = 10
update tb_paymentPolicy pp set pp.provider = 'o2' where pp.segment is not null and pp.communityID = 10

 -- IMP-1261 [MOBILE WEB] Error Messaging
alter table tb_paymentDetails add column errorCode varchar(255);
