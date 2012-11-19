CREATE TABLE device_user_data (
  `id` int unsigned NOT NULL auto_increment,
  `community_url` varchar(255) NOT NULL,
  `user_id` int NOT NULL,
  `xtify_token` char(255) NOT NULL UNIQUE,
  `device_uid` char(255) NOT NULL,
  PRIMARY KEY  (`id`),
  FOREIGN KEY (user_id) REFERENCES tb_users(i)
) DEFAULT CHARSET=utf8 AUTO_INCREMENT=1;