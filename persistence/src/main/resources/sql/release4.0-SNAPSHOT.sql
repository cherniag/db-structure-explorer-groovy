 -- Final insert of the release version
insert into system (release_time_millis, version, release_name) values(unix_timestamp(now()), "4.0-SN", "4.0-SN");

 -- IMP-1776 [Server] Update the Get Chart command to include Video
alter table tb_files add column duration int unsigned not null;
alter table tb_charts modify column i int unsigned not null;
alter table tb_chartDetail modify column chart int unsigned not null;

 -- http://jira.musicqubed.com/browse/IMP-1784
 -- [Server] Adjust payment system and jobs to support new 4G payment options
alter table tb_paymentpolicy add column tariff char(255);

update tb_paymentpolicy set tariff='_3G' where provider='o2';