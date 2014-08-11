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

update cn_service.tb_media
set trackId = 127663 where ISRC = 'USAT21001886';

update cn_service.tb_media
set trackId = 127653 where ISRC = 'USAT21001269';

update cn_service.tb_media
set trackId = 43806 where ISRC = 'GBAWV9902021';

update cn_service.tb_media
set trackId = 23673 where ISRC = 'USAT20904033';

update cn_service.tb_media
set trackId = 39715 where ISRC = 'GBAWV9902019';

update cn_service.tb_media
set trackId = 113946 where ISRC = 'USTB10300119';

update cn_service.tb_media
set trackId = 49271 where ISRC = 'USWB10001880';

update cn_service.tb_media
set trackId = 56628 where ISRC = 'USWB10905329';

update cn_service.tb_media
set trackId = 200083 where ISRC = 'USAT20100092';

update cn_service.tb_media
set trackId = 39708 where ISRC = 'USRE10901161';

update cn_service.tb_media
set trackId = 39742 where ISRC = 'GBAHT0600164';

update cn_service.tb_media
set trackId = 54863 where ISRC = 'USWB19903319';

update cn_service.tb_media
set trackId = 41442 where ISRC = 'USAT20303122';

update cn_service.tb_media
set trackId = 127654 where ISRC = 'IEABD0100002';

update cn_service.tb_media
set trackId = 57336 where ISRC = 'GBAAP9000008';

update cn_service.tb_media
set trackId = 353147 where ISRC = 'VIDEO181021';

update cn_service.tb_media
set trackId = 127664 where ISRC = 'USAT21001885';

update cn_service.tb_media
set trackId = 82803 where ISRC = 'GBUM71204768';

update cn_service.tb_media
set trackId = 813584 where ISRC = 'GBAAP9700206';

update cn_service.tb_media
set trackId = 681426 where ISRC = 'GBCRL0800322';

update cn_service.tb_media
set trackId = 353146 where ISRC = 'VIDEO181020';

update cn_service.tb_media tb
set tb.trackId = (select tm.id from cn_cms.Track tm where tm.isrc = tb.isrc order by tm.id limit 1)
where tb.trackId is null;

alter table tb_media drop index `isrc`;

ALTER TABLE tb_media ADD UNIQUE trackId_idx (trackId);

update  cn_service.tb_media tb
LEFT JOIN cn_cms.Track tr on (tb.trackId = tr.id)
set tb.trackId = null
where tr.id is null and tb.trackId is not null
