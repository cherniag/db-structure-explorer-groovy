CREATE TABLE `fat_job_trigger_request` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `job_name` varchar(255) NOT NULL,
  `group_name` varchar(255) NOT NULL,
  `execution_timestamp` bigint(20) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;