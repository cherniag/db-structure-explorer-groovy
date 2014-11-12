-- SRV-232
alter table tb_users add column uuid varchar(64);
update tb_users set uuid = uuid() where uuid is null;
alter table tb_users modify column uuid varchar(64) not null;
