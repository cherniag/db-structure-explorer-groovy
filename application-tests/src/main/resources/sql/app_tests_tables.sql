create table fat_currentPhone(
	phone_suffix bigint primary key auto_increment
);

CREATE TABLE `fat_job_trigger_request` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `job_name` varchar(255) NOT NULL,
  `group_name` varchar(255) NOT NULL,
  `execution_timestamp` bigint(20) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

create table fat_email(
	id bigint primary key auto_increment,
  `from_` varchar(100),
  `tos` varchar(512),
  `subject` varchar(255),
  `body` varchar(1024),
  `send_time` DATETIME,
  `model` varchar(2048)
);
