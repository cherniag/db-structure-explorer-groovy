insert into system (release_time_millis, version, release_name) values(unix_timestamp(now()), "5.5.0", "5.5.0");

create table fat_email(
	id bigint primary key auto_increment,
  `from_` varchar(100),
  `tos` varchar(512),
  `subject` varchar(255),
  `body` varchar(1024),
  `send_time` DATETIME,
  `model` varchar(2048)
);

ALTER TABLE tb_media MODIFY COLUMN label tinyint(3) unsigned;
update tb_media set label = null where label = 0;

alter table tb_media drop index `isrc`;

ALTER TABLE tb_media ADD UNIQUE trackId_idx (trackId);

update  cn_service.tb_media tb
LEFT JOIN cn_cms.Track tr on (tb.trackId = tr.id)
set tb.trackId = null
where tr.id is null and tb.trackId is not null
