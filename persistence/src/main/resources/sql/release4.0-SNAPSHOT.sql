 -- Final insert of the release version
insert into system (release_time_millis, version, release_name) values(unix_timestamp(now()), "4.0-SN", "4.0-SN");

 -- http://jira.musicqubed.com/browse/IMP-1784
 -- [Server] Adjust payment system and jobs to support new 4G payment options
alter table tb_paymentpolicy add column tariff char(255);

update tb_paymentpolicy set tariff='_3G' where provider='o2';