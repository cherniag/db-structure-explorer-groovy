-- monitoring first device login and user login
alter table tb_users add column firstDeviceLoginMillis BIGINT;
alter table tb_users add column firstUserLoginMillis BIGINT;

-- CL-6820 Tracks attached to web store item are not downloaded
alter table tb_submittedPayments add column offerId INT;
alter table tb_pendingPayments add column offerId INT;


-- 
insert into tb_accountLogTypes (i,name) values (8, 'Offer Purchase');

--
alter table tb_users add column isFreeTrial bit(1) NOT NULL DEFAULT b'0';

--
update messages set publishTimeMillis = UNIX_TIMESTAMP(STR_TO_DATE(FROM_UNIXTIME(publishTimeMillis/1000,'%Y %D %M'),'%Y %D %M'))*1000;

insert into system (release_time_millis, version, release_name) values(unix_timestamp(now()), "3.4.1", "Big Eagle Fix");