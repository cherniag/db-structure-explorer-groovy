insert into system (release_time_millis, version, release_name) values(unix_timestamp(now()), "5.4.9", "5.4.9");

create table fat_email(
	id bigint primary key auto_increment,
  `from_` varchar(100),
  `tos` varchar(512),
  `subject` varchar(255),
  `body` varchar(1024),
  `send_time` DATETIME,
  `model` varchar(2048)
);

