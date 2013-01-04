-- Final insert of the release version
insert into system (release_time_millis, version, release_name) values(unix_timestamp(now()), "3.6.0-SN", "3.6.0-SN");

alter table tb_media add column amazonUrl varchar(255);
alter table tb_users add column activation_status varchar(255);

alter table tb_users add column provider varchar(255);
alter table tb_users add column contract varchar(255);