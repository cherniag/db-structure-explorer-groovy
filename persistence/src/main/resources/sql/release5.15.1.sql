insert into system (release_time_millis, version, release_name) values(unix_timestamp(), "5.15.1", "5.15.1");
-- http://jira.musicqubed.com/browse/SRV-592: [SERVER] Force upgrade mtv1 old clients to 2.0 ver.

SET autocommit = 0;
START TRANSACTION;
/*setup messages*/
set @last_id_1 = (select coalesce(max(id), 0) from client_version_messages) + 1;
insert into client_version_messages(id, message_key, url) values(@last_id_1, 'service.config.mtv.tracks.FORCED_UPDATE.IOS.1.1.0', 'https://itunes.apple.com/gb/app/mtv-trax-free-music-player/id925265172');

set @last_id_2 = (select coalesce(max(id), 0) from client_version_messages) + 1;
insert into client_version_messages(id, message_key, url) values(@last_id_2, 'service.config.mtv.tracks.FORCED_UPDATE.IOS.1.2.0', 'https://itunes.apple.com/gb/app/mtv-trax-free-music-player/id925265172');

set @last_id_3 = (select coalesce(max(id), 0) from client_version_messages) + 1 ;
insert into client_version_messages(id, message_key, url) values(@last_id_3, 'service.config.mtv.tracks.FORCED_UPDATE.ANDROID.1.0.0', 'https://play.google.com/store/apps/details?id=com.musicqubed.mtv');

set @last_id_4 = (select coalesce(max(id), 0) from client_version_messages) + 1 ;
insert into client_version_messages(id, message_key, url) values(@last_id_4, 'service.config.mtv.tracks.FORCED_UPDATE.ANDROID.1.1.0', 'https://play.google.com/store/apps/details?id=com.musicqubed.mtv');

set @last_id_5 = (select coalesce(max(id), 0) from client_version_messages) + 1 ;
insert into client_version_messages(id, message_key, url) values(@last_id_5, 'service.config.mtv.tracks.FORCED_UPDATE.ANDROID.1.2.0', 'https://play.google.com/store/apps/details?id=com.musicqubed.mtv');

/*setup client versions*/
set @community_id = (select id from tb_communities where name='mtv1');
set @ios = (select i from tb_deviceTypes where name='IOS');
set @android = (select i from tb_deviceTypes where name='ANDROID');

insert into client_version_info (device_type_id,    community_id,  major_number, minor_number, revision_number, qualifier, message_id, status,          application_name)
                         values ( @ios,             @community_id, 1,            1,            0,               null,      @last_id_1, 'FORCED_UPDATE', 'mtv-tracks'),
                                ( @ios,             @community_id, 1,            2,            0,               null,      @last_id_2, 'FORCED_UPDATE', 'mtv-tracks'),
                                ( @android,         @community_id, 1,            0,            0,               null,      @last_id_3, 'FORCED_UPDATE', 'mtv-tracks'),
                                ( @android,         @community_id, 1,            1,            0,               null,      @last_id_4, 'FORCED_UPDATE', 'mtv-tracks'),
                                ( @android,         @community_id, 1,            2,            0,               null,      @last_id_5, 'FORCED_UPDATE', 'mtv-tracks');

COMMIT;
SET autocommit = 1;
-- End of SRV-592
