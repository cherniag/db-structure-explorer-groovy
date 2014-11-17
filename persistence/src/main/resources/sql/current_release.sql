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
)
