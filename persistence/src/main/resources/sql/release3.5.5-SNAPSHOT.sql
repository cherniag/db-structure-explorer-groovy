-- Final insert of the release version
-- insert into system (release_time_millis, version, release_name) values(unix_timestamp(now()), "3.5.5-SN", "Impetuous Zebra Omega");

alter table tb_mediaLog add index tb_mediaLog_PK_mediaUID (mediaUID), add constraint tb_mediaLog_U_mediaUID foreign key (mediaUID) references tb_media (i);
alter table tb_drm add index tb_drm_PK_media (media), add constraint tb_drm_U_media foreign key (media) references tb_media (i);