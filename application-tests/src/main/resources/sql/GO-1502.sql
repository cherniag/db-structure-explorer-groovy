create table fat_currentPhone(
	phone_suffix int(9) primary key auto_increment
);

create table fat_email(
	id bigint primary key auto_increment,
  `from_` varchar(100),
  `tos` varchar(512),
  `subject` varchar(255),
  `body` varchar(1024),
  `send_time` DATETIME,
  `model` varchar(2048)
);
