-- SRV-383
create table user_referrals(
  id BIGINT(20) NOT NULL AUTO_INCREMENT,
  user_id INT(10) UNSIGNED NOT NULL,
  community_id INT(11) NOT NULL,
  contact varchar(255) NOT NULL,
  provider_type varchar(255) NOT NULL,
  state varchar(255) NOT NULL,
  create_timestamp bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `user_referrals-user_id_fk` FOREIGN KEY (`user_id`) REFERENCES `tb_users`(i),
  CONSTRAINT `user_referrals-community_fk` FOREIGN KEY (`community_id`) REFERENCES `tb_communities`(id),
  CONSTRAINT `user_referrals-comm_con_uk` UNIQUE (`user_id`, `contact`)
);