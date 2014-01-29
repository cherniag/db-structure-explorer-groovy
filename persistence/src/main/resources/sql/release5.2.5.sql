CREATE TABLE `tb_tasks` (
  `taskType` varchar(50) NOT NULL,
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `creationTimestamp` bigint(20) NOT NULL,
  `executionTimestamp` bigint(20) NOT NULL,
  `taskStatus` char(25) DEFAULT NULL,
  `user_id` int(10) unsigned DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `tb_tasks_pk` (`user_id`),
  CONSTRAINT `tbTasksUserId_tbUsersI_fk` FOREIGN KEY (`user_id`) REFERENCES `tb_users` (`i`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;