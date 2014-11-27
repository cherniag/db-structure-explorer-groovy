insert into system (release_time_millis, version, release_name) values(unix_timestamp(now()), "5.11", "5.11");

-- SRV-232
alter table tb_users add column uuid varchar(64);
update tb_users set uuid = uuid() where uuid is null;
alter table tb_users modify column uuid varchar(64) not null;

-- SRV-226
create table apps_flyer_data(
  id BIGINT(20) NOT NULL AUTO_INCREMENT,
  user_id INT(10) UNSIGNED NOT NULL,
  apps_flyer_uid VARCHAR(255) NOT NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `fk_apps_flyer_user_id` FOREIGN KEY (`user_id`) REFERENCES `tb_users`(i),
  CONSTRAINT `unique_apps_flyer_user_id` UNIQUE (`user_id`)
);
-- SRV-374
UPDATE tb_submittedPayments sp join tb_users u on sp.userId = u.i join tb_userGroups ug on u.userGroup = ug.id join tb_communities c on ug.community = c.id
SET
  duration = 1,
  duration_unit = 'WEEKS'
where
  sp.paymentSystem='iTunesSubscription'
  and sp.duration = 0
  and sp.duration_unit = 'WEEKS'
  and c.id in (12,13,14,15);

UPDATE tb_submittedPayments SET
  duration = 1,
  duration_unit = 'MONTHS'
where
  paymentSystem='iTunesSubscription'
  and duration = 0
  and duration_unit = 'WEEKS';
