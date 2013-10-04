 -- Final insert of the release version
insert into system (release_time_millis, version, release_name) values(unix_timestamp(now()), "4.1.1-SN", "4.1.1-SN");

-- IMP-1532 - Exception appears during merge accounts
-- http://jira.musicqubed.com/browse/IMP-1532
insert into tb_accountLogTypes values (13, 'ACCOUNT_MERGE');

alter table tb_accountLog add column description varchar (1000) default null;

